package sn.yooni.dto.response; // Déclaration du package dto.response (réponses HTTP sortantes)

// Réponse générique et normalisée pour les opérations sans corps de données particulier
public record MessageResponse(
        String message // Message lisible par l'utilisateur (confirmation, information...)
) { } // Enregistrement immuable, sérialisé automatiquement par Jackson