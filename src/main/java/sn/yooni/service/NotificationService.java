package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.dto.NotificationDTO; // DTO de réponse pour les notifications
import sn.yooni.exception.BusinessException; // Erreur métier portant un code HTTP
import sn.yooni.model.Notification; // Entité notification
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.repository.NotificationRepository; // Dépôt des notifications
import sn.yooni.repository.UserRepository; // Dépôt des comptes utilisateurs

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

import java.util.List; // Liste de notifications

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépendances
public class NotificationService { // Services de consultation et de gestion des notifications

    private final NotificationRepository notificationRepository; // Accès aux notifications
    private final UserRepository userRepository; // Accès aux comptes utilisateurs

    // Toutes les notifications du compte courant (les plus récentes en premier)
    @Transactional(readOnly = true) // Lecture seule
    public List<NotificationDTO> mesNotifications(Long userId) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(findUser(userId)).stream()
                .map(this::toDto) // Convertit chaque entité en DTO
                .toList(); // Liste immuable des notifications
    }

    // Notifications non lues du compte courant (file d'alertes)
    @Transactional(readOnly = true) // Lecture seule
    public List<NotificationDTO> nonLues(Long userId) {
        return notificationRepository.findByUserAndLueFalseOrderByCreatedAtDesc(findUser(userId)).stream()
                .map(this::toDto) // Convertit chaque entité en DTO
                .toList(); // Liste immuable des non lues
    }

    // Marque une notification comme lue (uniquement la sienne)
    @Transactional // Écriture en base
    public void marquerLue(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Notification introuvable."));
        // Seul le propriétaire de la notification peut la marquer comme lue
        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Vous ne pouvez pas consulter cette notification.");
        }
        if (!Boolean.TRUE.equals(notification.getLue())) {
            notification.setLue(true); // Marque comme lue
            notificationRepository.save(notification); // Persiste la modification
        }
    }

    // Charge un compte utilisateur (404 si absent)
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
    }

    // Convertit une entité Notification en DTO (aucune donnée sensible)
    private NotificationDTO toDto(Notification notification) {
        return new NotificationDTO(
                notification.getId(), // Identifiant
                notification.getTitre(), // Titre court
                notification.getMessage(), // Contenu
                notification.getTypeNotification(), // Catégorie
                notification.getLue(), // État de lecture
                notification.getCreatedAt() // Date d'émission
        );
    }

}