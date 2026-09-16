package sn.yooni.util; // Déclaration du package utilitaire (helpers sans état)

import sn.yooni.model.Notification; // Entité Notification
import sn.yooni.model.TypeNotification; // Énumération des types de notification
import sn.yooni.model.User; // Entité utilisateur destinataire

import lombok.experimental.UtilityClass; // Indique que la classe est un utilitaire pur

@UtilityClass // Empêche l'instanciation de la classe (toutes les méthodes sont statiques)
public class NotificationUtil { // Fabrique de notifications pour la logique métier

    // Construit une notification avec tous les champs (pattern Builder Lombok sur l'entité)
    public Notification create(User user, String titre, String message, TypeNotification type) {
        return Notification.builder()
                .user(user) // Destinataire de la notification
                .titre(titre) // Titre court (ex: "Livraison en cours")
                .message(message) // Corps du message détaillé
                .typeNotification(type) // Type de la notification
                .lue(false) // Non lue par défaut
                .build(); // Instance prête à être persistée
    }

    // Crée une notification de course acceptée par un livreur
    public Notification courseAcceptee(User client, String nomLivreur) {
        return create(client,
                "Course acceptée",
                "Votre course a été acceptée par " + nomLivreur + ". Le chauffeur se rend vers vous.",
                TypeNotification.COURSE_ACCEPTEE);
    }

    // Crée une notification de livraison terminée
    public Notification livraisonTerminee(User user, String details) {
        return create(user,
                "Livraison terminée",
                "Votre livraison est terminée. " + details,
                TypeNotification.LIVRAISON_TERMINEE);
    }

}