package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.dto.PaiementDTO; // DTO de réponse pour les paiements
import sn.yooni.dto.request.InitierPaiementRequest; // Données de la demande de paiement
import sn.yooni.exception.BusinessException; // Erreur métier portant un code HTTP
import sn.yooni.model.Livraison; // Entité livraison
import sn.yooni.model.Paiement; // Entité paiement
import sn.yooni.model.StatutLivraison; // États d'avancement d'une course
import sn.yooni.model.StatutPaiement; // États financiers d'un paiement
import sn.yooni.model.TypeNotification; // Type de notification
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.model.UserRole; // Rôles applicatifs
import sn.yooni.repository.LivraisonRepository; // Dépôt des livraisons
import sn.yooni.repository.NotificationRepository; // Dépôt des notifications
import sn.yooni.repository.PaiementRepository; // Dépôt des paiements
import sn.yooni.repository.UserRepository; // Dépôt des comptes utilisateurs
import sn.yooni.util.NotificationUtil; // Fabrique de notifications métier

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

import java.util.List; // Liste de paiements
import java.util.UUID; // Génération de références transactionnelles simulées

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépendances
public class PaiementService { // Services de gestion des paiements (Wave, Orange Money, Cash)

    private final PaiementRepository paiementRepository; // Accès aux paiements
    private final LivraisonRepository livraisonRepository; // Accès aux livraisons
    private final UserRepository userRepository; // Accès aux comptes utilisateurs
    private final NotificationRepository notificationRepository; // Accès aux notifications
    private final AuditLogService auditLogService; // Traçabilité des opérations financières

    // Enregistre le paiement d'une livraison terminée (simulation d'un appel mobile money)
    @Transactional // Écriture en base (paiement + notification + audit)
    public PaiementDTO initier(Long userId, InitierPaiementRequest req) {
        User user = findUser(userId); // Compte client qui paie
        Livraison livraison = livraisonRepository.findById(req.livraisonId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Livraison introuvable."));
        // Seul le client propriétaire de la course peut payer
        if (!livraison.getClient().getUser().getId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Seul le client propriétaire peut régler cette course.");
        }
        // Un paiement ne peut être initié que sur une course terminée
        if (livraison.getStatut() != StatutLivraison.TERMINEE) {
            throw new BusinessException(HttpStatus.CONFLICT,
                    "La course doit être terminée avant d'initier un paiement.");
        }
        // Un seul paiement par course (contrainte unique en base)
        if (paiementRepository.findByLivraison(livraison).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "Un paiement existe déjà pour cette course.");
        }
        // Simulation d'un identifiant de transaction fourni par l'opérateur (Wave, Orange Money...)
        String reference = "YOO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Paiement paiement = Paiement.builder() // Construit l'objet paiement
                .livraison(livraison) // Course concernée
                .montant(livraison.getPrix()) // Montant identique au tarif de la course
                .moyenPaiement(req.moyenPaiement()) // Méthode choisie par le client
                .statutPaiement(StatutPaiement.PAYE) // Paiement confirmé (simulation)
                .referenceTransaction(reference) // Référence externe simulée
                .build(); // Instance prête à être persistée

        Paiement sauvegarde = paiementRepository.save(paiement); // Persiste le paiement

        // Notifie le client de la confirmation du paiement
        notificationRepository.save(NotificationUtil.create(user,
                "Paiement confirmé",
                "Votre paiement de " + livraison.getPrix() + " FCFA pour la course "
                        + req.livraisonId() + " via " + req.moyenPaiement() + " a été enregistré.",
                TypeNotification.PAIEMENT_VALIDE));

        // Traçabilité de l'opération financière dans le journal d'audit
        auditLogService.record("PAIEMENT_ENREGISTRE", "Paiement", sauvegarde.getId(),
                "Paiement de " + livraison.getPrix() + " FCFA via " + req.moyenPaiement()
                        + " — référence " + reference + ".", user);

        return toDto(sauvegarde); // Retourne le paiement confirmé
    }

    // Consulte le paiement associé à une livraison (propriétaire ou administrateur)
    @Transactional(readOnly = true) // Lecture seule
    public PaiementDTO paiementLivraison(Long userId, Long livraisonId) {
        User user = findUser(userId); // Compte demandeur
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Livraison introuvable."));
        boolean proprietaire = livraison.getClient().getUser().getId().equals(userId);
        // Seul le client propriétaire ou un administrateur peut consulter les détails du paiement
        if (!proprietaire && user.getRole() != UserRole.ROLE_ADMIN) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Accès interdit à ce paiement.");
        }
        Paiement paiement = paiementRepository.findByLivraison(livraison)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Aucun paiement enregistré pour cette course."));
        return toDto(paiement); // Retourne les détails du paiement
    }

    // Liste tous les paiements (réservée à l'administration)
    @Transactional(readOnly = true) // Lecture seule
    public List<PaiementDTO> liste() {
        return paiementRepository.findAll().stream()
                .map(this::toDto) // Convertit chaque entité en DTO
                .toList(); // Liste complète
    }

    // Charge un compte utilisateur (404 si absent)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
    }

    // Convertit une entité Paiement en DTO sécurisé
    private PaiementDTO toDto(Paiement paiement) {
        return new PaiementDTO(
                paiement.getId(), // Identifiant
                paiement.getLivraison().getId(), // Identifiant de la livraison
                paiement.getMontant(), // Montant en FCFA
                paiement.getMoyenPaiement(), // Méthode de paiement
                paiement.getStatutPaiement(), // État du paiement
                paiement.getReferenceTransaction(), // Référence externe
                paiement.getCreatedAt() // Date d'initiation
        );
    }

}