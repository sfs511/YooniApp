package sn.yooni.dto; // Déclaration du package dto (objets de transfert de données)

import sn.yooni.model.TypeNotification; // Importation de l'énumération TypeNotification

import java.time.LocalDateTime; // Horodatage de création de la notification

// Représentation publique d'une notification destinée à un utilisateur
public record NotificationDTO(
        Long id, // Identifiant technique de la notification
        String titre, // Titre court de la notification
        String message, // Contenu du message
        TypeNotification typeNotification, // Catégorie de la notification
        Boolean lue, // Indique si la notification a été consultée
        LocalDateTime createdAt // Date d'émission
) { } // Enregistrement immuable sérialisé par Jackson