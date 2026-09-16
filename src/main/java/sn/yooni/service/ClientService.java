package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.dto.ClientDTO; // DTO de réponse pour les profils clients
import sn.yooni.exception.BusinessException; // Erreur métier portant un code HTTP
import sn.yooni.model.Client; // Entité profil client
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.repository.ClientRepository; // Dépôt des profils clients
import sn.yooni.repository.UserRepository; // Dépôt des comptes utilisateurs

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

import java.util.List; // Liste de profils clients

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépôts
public class ClientService { // Services métier des profils clients

    private final ClientRepository clientRepository; // Accès aux profils clients
    private final UserRepository userRepository; // Accès aux comptes utilisateurs

    // Retourne le profil client rattaché au compte authentifié
    @Transactional(readOnly = true) // Lecture seule, aucune écriture en base
    public ClientDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Profil client introuvable."));
        return toDto(client, user); // Convertit l'entité en DTO sécurisé
    }

    // Liste tous les profils clients (administration)
    @Transactional(readOnly = true)
    public List<ClientDTO> listAll() {
        return clientRepository.findAll().stream() // Parcourt tous les profils
                .map(client -> toDto(client, client.getUser())) // Convertit chacun en DTO
                .toList(); // Retourne la liste immuable des DTO
    }

    // Convertit une entité Client (avec son compte User) en DTO sécurisé (aucune donnée sensible)
    private ClientDTO toDto(Client client, User user) {
        return new ClientDTO(
                client.getId(), // Identifiant du profil client
                client.getNom(), // Nom de famille
                client.getPrenom(), // Prénom
                user.getEmail(), // E-mail du compte associé
                user.getTelephone(), // Téléphone du compte associé
                client.getPhotoUrl() // URL de la photo (peut être null)
        );
    }

}