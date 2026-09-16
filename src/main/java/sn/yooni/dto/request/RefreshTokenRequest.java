package sn.yooni.dto.request; // Déclaration du package dto.request (requêtes HTTP entrantes)

import jakarta.validation.constraints.NotBlank; // Validation de non-vide

// Requête de rafraîchissement d'une session à partir d'un Refresh Token
public record RefreshTokenRequest(

        @NotBlank(message = "Le refresh token est obligatoire.") // Champ obligatoire
        String refreshToken // Jeton de rafraîchissement précédemment émis lors du login
) { } // Enregistrement immuable, sérialisé automatiquement par Jackson