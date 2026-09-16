package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.dto.LivraisonDTO; // DTO de réponse pour les livraisons
import sn.yooni.dto.request.CreerLivraisonRequest; // Données de la demande de course
import sn.yooni.exception.BusinessException; // Erreur métier portant un code HTTP
import sn.yooni.model.Client; // Entité profil client
import sn.yooni.model.Livraison; // Entité livraison / course
import sn.yooni.model.Livreur; // Entité profil livreur
import sn.yooni.model.StatutLivraison; // États d'avancement d'une course
import sn.yooni.model.StatutLivreur; // États opérationnels du livreur
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.model.UserRole; // Rôles du système (CLIENT, LIVREUR, ADMIN)
import sn.yooni.repository.ClientRepository; // Dépôt des profils clients
import sn.yooni.repository.LivraisonRepository; // Dépôt des livraisons
import sn.yooni.repository.LivreurRepository; // Dépôt des profils livreurs
import sn.yooni.repository.NotificationRepository; // Dépôt des notifications
import sn.yooni.repository.UserRepository; // Dépôt des comptes utilisateurs
import sn.yooni.util.DistanceCalculator; // Calcul Haversine (distance, durée, tarif)
import sn.yooni.util.NotificationUtil; // Fabrique de notifications métier

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

import java.math.BigDecimal; // Montant en FCFA précis
import java.math.RoundingMode; // Arrondi des calculs financiers
import java.util.List; // Liste de livraisons

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépendances
public class LivraisonService { // Services métier des livraisons et courses VTC

    private final LivraisonRepository livraisonRepository; // Accès aux livraisons
    private final ClientRepository clientRepository; // Accès aux profils clients
    private final LivreurRepository livreurRepository; // Accès aux profils livreurs
    private final UserRepository userRepository; // Accès aux comptes utilisateurs
    private final NotificationRepository notificationRepository; // Accès aux notifications
    private final AuditLogService auditLogService; // Traçabilité des actions

    // Crée une demande de course : calcul de distance, durée estimée et tarif en FCFA
    @Transactional // Écriture en base (insertion de la livraison + trace d'audit)
    public LivraisonDTO creer(Long userId, CreerLivraisonRequest req) {
        Client client = findClient(userId); // Profil du client demandeur
        // Distance à vol d'oiseau entre départ et arrivée (formule de Haversine)
        double distanceKm = Math.round(DistanceCalculator.calculateDistance(
                req.latDepart(), req.lngDepart(), req.latArrivee(), req.lngArrivee()) * 100.0) / 100.0;
        int dureeEstimeeMin = (int) Math.round(DistanceCalculator.estimateDuration(distanceKm, 30.0)); // Durée estimée
        // Tarif = forfait de départ + prix du kilomètre parcouru, arrondi à 2 décimales
        BigDecimal prix = BigDecimal.valueOf(DistanceCalculator.estimateFare(distanceKm))
                .setScale(2, RoundingMode.HALF_UP);

        Livraison livraison = Livraison.builder() // Construit la nouvelle demande
                .client(client) // Client demandeur
                .adresseDepart(req.adresseDepart()) // Libellé du départ
                .latDepart(req.latDepart()) // Latitude de départ
                .lngDepart(req.lngDepart()) // Longitude de départ
                .adresseArrivee(req.adresseArrivee()) // Libellé de l'arrivée
                .latArrivee(req.latArrivee()) // Latitude d'arrivée
                .lngArrivee(req.lngArrivee()) // Longitude d'arrivée
                .prix(prix) // Tarif total FCFA
                .statut(StatutLivraison.EN_ATTENTE) // En attente d'un livreur
                .distanceKm(distanceKm) // Distance totale du trajet
                .dureeEstimeeMin(dureeEstimeeMin) // Durée estimée du trajet
                .build(); // Instance prête à être persistée

        Livraison sauvee = livraisonRepository.save(livraison); // Persiste la demande
        // Trace d'audit pour la traçabilité des créations
        auditLogService.record("LIVRAISON_CREEE", "Livraison", sauvee.getId(),
                "Course demandée de " + req.adresseDepart() + " vers " + req.adresseArrivee()
                        + " — tarif " + prix + " FCFA.", client.getUser());
        return toDto(sauvee); // Retourne la demande complète (statut EN_ATTENTE)
    }

    // Historique des courses du compte courant (client ou livreur)
    @Transactional(readOnly = true) // Lecture seule
    public List<LivraisonDTO> mesLivraisons(Long userId) {
        User user = findUser(userId); // Compte connecté
        // Distingue les deux profils par leur rôle pour interroger le bon historique
        if (user.getRole() == UserRole.ROLE_CLIENT) {
            return clientRepository.findByUser(user) // Profil client
                    .map(client -> livraisonRepository.findByClientOrderByCreatedAtDesc(client)) // Historique client
                    .map(list -> list.stream().map(this::toDto).toList()) // Conversion en DTO
                    .orElse(List.of()); // Aucun profil : liste vide
        }
        if (user.getRole() == UserRole.ROLE_LIVREUR) {
            return livreurRepository.findByUser(user) // Profil livreur
                    .map(livreur -> livraisonRepository.findByLivreurOrderByCreatedAtDesc(livreur)) // Historique livreur
                    .map(list -> list.stream().map(this::toDto).toList()) // Conversion en DTO
                    .orElse(List.of()); // Aucun profil : liste vide
        }
        return List.of(); // Administrateur : historique non concerné par ce endpoint
    }

    // Détail d'une livraison par son identifiant.
    // Contrôle d'accès (anti-IDOR) : seuls le client propriétaire de la course,
    // le livreur assigné ou un administrateur peuvent consulter ce détail.
    @Transactional(readOnly = true) // Lecture seule
    public LivraisonDTO getLivraison(Long userId, Long livraisonId) {
        User user = findUser(userId); // Compte demandeur
        Livraison livraison = findLivraison(livraisonId); // Course concernée
        boolean estAdmin = user.getRole() == UserRole.ROLE_ADMIN; // L'administrateur accède à tout
        boolean estClientProprietaire = livraison.getClient() != null
                && livraison.getClient().getUser().getId().equals(userId); // Client demandeur de la course
        boolean estLivreurAssigne = livraison.getLivreur() != null
                && livraison.getLivreur().getUser().getId().equals(userId); // Livreur qui exécute la course
        // Sans lien avec la course, aucune donnée (adresses, tarif, client) ne doit fuiter
        if (!estAdmin && !estClientProprietaire && !estLivreurAssigne) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Accès refusé à cette livraison.");
        }
        return toDto(livraison); // Charge et convertit
    }

    // Un livreur accepte une demande en attente (auto-assignation)
    @Transactional // Écriture en base (livraison + livreur + notification + audit)
    public LivraisonDTO accepter(Long userId, Long livraisonId) {
        Livreur livreur = findLivreur(userId); // Livreur authentifié
        // Un livreur suspendu ne peut jamais accepter de courses
        if (livreur.getStatut() == StatutLivreur.SUSPENDU) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Compte suspendu : prises de courses interdites.");
        }
        if (!Boolean.TRUE.equals(livreur.getDisponible())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Activez votre disponibilité avant d'accepter une course.");
        }
        Livraison livraison = findLivraison(livraisonId); // Course à accepter
        // Acceptation atomique et conditionnelle : la course n'est attribuée que si elle
        // est toujours EN_ATTENTE au moment de l'UPDATE (une seule requête SQL).
        // Cela empêche deux livreurs de prendre la même course en cas de requête simultanée.
        int misesAJour = livraisonRepository.claimByIdAndStatut(
                livraisonId, livreur, StatutLivraison.EN_ATTENTE, StatutLivraison.ACCEPTEE);
        if (misesAJour == 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "Cette course a déjà été prise en charge.");
        }
        // L'UPDATE atomique a vidé le contexte de persistance (clearAutomatically) :
        // la course est rechargée, et le livreur (détaché) est explicitement re-persisté
        // pour éviter que sa mise à jour de disponibilité soit perdue.
        livraison = findLivraison(livraisonId);
        livreur.setDisponible(false); // Plus disponible pour d'autres demandes
        livreur.setStatut(StatutLivreur.EN_COURSE); // En cours d'exécution
        livreurRepository.save(livreur); // Re-persiste le livreur (détaché après l'UPDATE atomique)
        // Notifie le client que sa course est prise en charge
        notificationRepository.save(NotificationUtil.courseAcceptee(
                livraison.getClient().getUser(), livreur.getPrenom() + " " + livreur.getNom()));
        auditLogService.record("LIVRAISON_ACCEPTEE", "Livraison", livraison.getId(),
                "Course acceptée par le livreur " + livreur.getPrenom() + " " + livreur.getNom() + ".", livreur.getUser());
        return toDto(livraison); // Course à l'état ACCEPTEE
    }

    // Le livreur assigné clôture la course : livreur à nouveau disponible, client notifié
    @Transactional // Écriture en base (livraison + livreur + notification + audit)
    public LivraisonDTO terminer(Long userId, Long livraisonId) {
        Livreur livreur = findLivreur(userId); // Livreur authentifié
        Livraison livraison = findLivraison(livraisonId); // Course concernée
        // Seul le livreur assigné peut clôturer sa propre course
        if (livraison.getLivreur() == null || !livraison.getLivreur().getId().equals(livreur.getId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Cette course n'est pas assignée à votre compte.");
        }
        // Seules les courses acceptées ou en cours peuvent être clôturées
        if (livraison.getStatut() != StatutLivraison.ACCEPTEE
                && livraison.getStatut() != StatutLivraison.EN_COURS) {
            throw new BusinessException(HttpStatus.CONFLICT, "Cette course ne peut pas être clôturée dans son état actuel.");
        }
        livraison.setStatut(StatutLivraison.TERMINEE); // Course terminée
        livreur.setDisponible(true); // Le livreur redevient disponible
        livreur.setStatut(StatutLivreur.DISPONIBLE); // En ligne et libre
        // Notifie le client de la fin de la course
        notificationRepository.save(NotificationUtil.livraisonTerminee(
                livraison.getClient().getUser(), "Colis ou passager déposé à destination."));
        auditLogService.record("LIVRAISON_TERMINEE", "Livraison", livraison.getId(),
                "Course clôturée par le livreur " + livreur.getPrenom() + " " + livreur.getNom() + ".", livreur.getUser());
        return toDto(livraison); // Course à l'état TERMINEE
    }

    // Annulation d'une course : par le client propriétaire ou par un administrateur
    @Transactional // Écriture en base (livraison + éventuel livreur + audit)
    public LivraisonDTO annuler(Long userId, Long livraisonId) {
        User user = findUser(userId); // Compte qui annule
        Livraison livraison = findLivraison(livraisonId); // Course concernée
        // Droit d'annulation : le client demandeur OU un administrateur
        boolean estClientProprietaire = livraison.getClient().getUser().getId().equals(userId);
        if (user.getRole() != UserRole.ROLE_ADMIN && !estClientProprietaire) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Vous ne pouvez pas annuler une course qui ne vous appartient pas.");
        }
        // Une course terminée ou déjà annulée est clôturée définitivement
        if (livraison.getStatut() == StatutLivraison.TERMINEE
                || livraison.getStatut() == StatutLivraison.ANNULEE) {
            throw new BusinessException(HttpStatus.CONFLICT, "Cette course est déjà clôturée.");
        }
        Livreur livreur = livraison.getLivreur(); // Livreur assigné (s'il y en a un)
        // Si le livreur avait déjà accepté, il est libéré et redevient disponible
        if (livreur != null && livreur.getStatut() == StatutLivreur.EN_COURSE) {
            livreur.setDisponible(true); // Disponible pour d'autres demandes
            livreur.setStatut(StatutLivreur.DISPONIBLE); // En ligne et libre
        }
        livraison.setStatut(StatutLivraison.ANNULEE); // Course annulée
        auditLogService.record("LIVRAISON_ANNULEE", "Livraison", livraison.getId(),
                "Course annulée par " + user.getEmail() + ".", user);
        return toDto(livraison); // Course à l'état ANNULEE
    }

    // Charge le profil client rattaché à un compte utilisateur
    private Client findClient(Long userId) {
        return clientRepository.findByUser(findUser(userId))
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Profil client introuvable."));
    }

    // Charge le profil livreur rattaché à un compte utilisateur
    private Livreur findLivreur(Long userId) {
        return livreurRepository.findByUser(findUser(userId))
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Profil livreur introuvable."));
    }

    // Charge un compte utilisateur (404 si absent)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
    }

    // Charge une livraison (404 si absente)
    private Livraison findLivraison(Long livraisonId) {
        return livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Livraison introuvable."));
    }

    // Convertit une entité Livraison en DTO sécurisé (aucune donnée sensible)
    private LivraisonDTO toDto(Livraison livraison) {
        return new LivraisonDTO(
                livraison.getId(), // Identifiant de la course
                livraison.getClient().getId(), // Identifiant du client demandeur
                livraison.getLivreur() != null ? livraison.getLivreur().getId() : null, // Livreur assigné (null si en attente)
                livraison.getAdresseDepart(), // Libellé du départ
                livraison.getLatDepart(), // Latitude de départ
                livraison.getLngDepart(), // Longitude de départ
                livraison.getAdresseArrivee(), // Libellé de l'arrivée
                livraison.getLatArrivee(), // Latitude d'arrivée
                livraison.getLngArrivee(), // Longitude d'arrivée
                livraison.getPrix(), // Tarif total FCFA
                livraison.getStatut(), // État d'avancement
                livraison.getDistanceKm(), // Distance estimée
                livraison.getDureeEstimeeMin(), // Durée estimée en minutes
                livraison.getCreatedAt() // Date de la demande
        );
    }

}