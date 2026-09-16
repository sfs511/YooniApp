package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.dto.LivreurDTO; // DTO de réponse pour les profils livreurs
import sn.yooni.exception.BusinessException; // Erreur métier portant un code HTTP
import sn.yooni.model.Livreur; // Entité profil livreur
import sn.yooni.model.StatutLivreur; // Énumération des états du livreur
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.model.UserRole; // Rôle de l'utilisateur (ADMIN, CLIENT, LIVREUR)
import sn.yooni.repository.LivreurRepository; // Dépôt des profils livreurs
import sn.yooni.repository.UserRepository; // Dépôt des comptes utilisateurs

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

import java.util.List; // Liste de profils livreurs

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépôts
public class LivreurService { // Services métier des profils livreurs

    private final LivreurRepository livreurRepository; // Accès aux profils livreurs
    private final UserRepository userRepository; // Accès aux comptes utilisateurs

    // Retourne le profil livreur rattaché au compte authentifié
    @Transactional(readOnly = true) // Lecture seule
    public LivreurDTO getProfile(Long userId) {
        return toDto(findByUser(userId)); // Charge et convertit le profil
    }

    // Retourne un profil livreur par son identifiant
    @Transactional(readOnly = true)
    public LivreurDTO getLivreur(Long livreurId) {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Livreur introuvable."));
        return toDto(livreur); // Convertit l'entité en DTO
    }

    // Liste des livreurs actuellement disponibles pour accepter des courses
    @Transactional(readOnly = true)
    public List<LivreurDTO> listDisponibles() {
        // Critère : disponible=true ET statut DISPONIBLE (en ligne, libre)
        return livreurRepository.findByDisponibleTrueAndStatut(StatutLivreur.DISPONIBLE).stream()
                .map(this::toDto) // Convertit chaque entité en DTO
                .toList(); // Retourne la liste immuable
    }

    // Active ou désactive la disponibilité d'un livreur (bascule en ligne / hors ligne).
    // Anti-IDOR : seul le livreur concerné (son propre profil) ou un administrateur peut
    // modifier la disponibilité. Sans ce contrôle, n'importe quel livreur pourrait
    // mettre en ligne/hors ligne le compte d'un autre chauffeur.
    @Transactional // Écriture en base
    public LivreurDTO toggleDisponibilite(Long userId, Long livreurId) {
        User user = findUser(userId); // Compte qui déclenche la bascule
        boolean estAdmin = user.getRole() == UserRole.ROLE_ADMIN;
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Livreur introuvable."));
        // Le demandeur doit être le propriétaire du profil ou un administrateur
        boolean estProprietaire = livreur.getUser().getId().equals(userId);
        if (!estAdmin && !estProprietaire) {
            throw new BusinessException(HttpStatus.FORBIDDEN,
                    "Vous ne pouvez pas modifier la disponibilité d'un autre livreur.");
        }
        // Un livreur suspendu ne peut pas basculer en ligne (décision administrative)
        if (livreur.getStatut() == StatutLivreur.SUSPENDU) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Compte suspendu : indisponible pour les courses.");
        }
        boolean nouveauEtat = !Boolean.TRUE.equals(livreur.getDisponible()); // Inverse l'état actuel
        livreur.setDisponible(nouveauEtat); // Applique le nouvel état de disponibilité
        // Synchronise le statut métier avec la disponibilité (DISPONIBLE ↔ INACTIF)
        livreur.setStatut(nouveauEtat ? StatutLivreur.DISPONIBLE : StatutLivreur.INACTIF);
        return toDto(livreurRepository.save(livreur)); // Persiste et retourne le profil mis à jour
    }

    // Utilitaire : charge un compte utilisateur (404 si absent)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
    }

    // Charge le profil livreur d'un compte utilisateur donné
    private Livreur findByUser(Long userId) {
        User user = findUser(userId);
        return livreurRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Profil livreur introuvable."));
    }

    // Convertit une entité Livreur en DTO sécurisé (aucune donnée sensible)
    private LivreurDTO toDto(Livreur livreur) {
        User user = livreur.getUser(); // Compte utilisateur associé (nom/prénom téléphone/e-mail)
        return new LivreurDTO(
                livreur.getId(), // Identifiant du profil livreur
                livreur.getNom(), // Nom de famille
                livreur.getPrenom(), // Prénom
                user.getEmail(), // E-mail du compte associé
                user.getTelephone(), // Téléphone du compte associé
                livreur.getPhotoUrl(), // URL de la photo (peut être null)
                livreur.getStatut(), // État opérationnel
                livreur.getNoteMoyenne(), // Note moyenne sur 5
                livreur.getDisponible() // Disponibilité pour les courses
        );
    }

}