package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.request.LoginRequest; // Requête de connexion
import sn.yooni.dto.request.RefreshTokenRequest; // Requête de rafraîchissement
import sn.yooni.dto.request.RegisterRequest; // Requête d'inscription
import sn.yooni.dto.response.JwtResponse; // Réponse standard de l'authentification
import sn.yooni.service.AuthService; // Service métier d'authentification

import jakarta.validation.Valid; // Déclenche la validation des DTO (@Valid)

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Code de statut HTTP créé
import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.web.bind.annotation.PostMapping; // Mappage des requêtes POST
import org.springframework.web.bind.annotation.RequestBody; // Désérialisation du corps JSON
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/auth") // Toutes les routes d'authentification partagent ce préfixe
@RequiredArgsConstructor // Constructeur généré pour l'injection d'AuthService
public class AuthController { // Exposition publique des endpoints d'authentification

    private final AuthService authService; // Service métier de gestion des sessions

    // POST /api/auth/register — création d'un compte client + ouverture de session
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request); // Créé le compte et émet les jetons
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Réponse 201 avec les jetons
    }

    // POST /api/auth/login — connexion e-mail/mot de passe
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request); // Vérifie les identifiants et émet les jetons
        return ResponseEntity.ok(response); // Réponse 200 avec les jetons
    }

    // POST /api/auth/refresh — renouvellement de la session via le Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        JwtResponse response = authService.refresh(request.refreshToken()); // Fait la rotation des jetons
        return ResponseEntity.ok(response); // Réponse 200 avec le nouveau couple de jetons
    }

    // POST /api/auth/logout — révocation du Refresh Token (déconnexion)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken()); // Révoque le jeton (idempotent)
        return ResponseEntity.ok().build(); // Réponse 200 sans corps
    }

}