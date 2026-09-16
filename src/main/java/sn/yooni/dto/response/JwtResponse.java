package sn.yooni.dto.response; // Déclaration du package dto.response (réponses HTTP sortantes)

import sn.yooni.dto.response.UserResponse; // Profil sécurisé de l'utilisateur authentifié

// Réponse standard de l'authentification : contient les deux jetons et le profil utilisateur
public record JwtResponse(
        String accessToken, // Jeton d'accès court (24h) à inclure dans l'en-tête Authorization
        String refreshToken, // Jeton de rafraîchissement long (7 jours) pour renouveler la session
        String tokenType, // Type de jeton : "Bearer"
        long expiresIn, // Durée de validité de l'access token en millisecondes
        UserResponse user // Profil sécurisé de l'utilisateur authentifié
) {
    // Fabrique statique pour alléger la construction dans les services
    public static JwtResponse of(String accessToken, String refreshToken, long expiresIn, UserResponse user) {
        return new JwtResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }

}