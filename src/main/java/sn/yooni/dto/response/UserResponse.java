package sn.yooni.dto.response; // Déclaration du package dto.response (réponses HTTP sortantes)

import com.fasterxml.jackson.annotation.JsonProperty; // Annotation pour spécifier les noms de champs JSON

import java.time.LocalDateTime; // Horodatage de création du compte
import sn.yooni.model.UserRole; // Rôle de l'utilisateur

// Représentation publique (sécurisée) d'un compte utilisateur — jamais de passwordHash ici
public record UserResponse(
        Long id, // Identifiant technique de l'utilisateur
        String email, // Adresse e-mail de connexion
        String telephone, // Numéro de téléphone
        UserRole role, // Rôle applicatif (ROLE_CLIENT, ROLE_LIVREUR, ROLE_ADMIN)
        @JsonProperty("actif") Boolean actif, // État du compte (true = actif)
        LocalDateTime createdAt // Date de création du compte
) { } // Enregistrement immuable sérialisé par Jackson