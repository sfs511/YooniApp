package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.LivreurDTO; // DTO de réponse pour les profils livreurs
import sn.yooni.security.UserPrincipal; // Utilisateur authentifié fourni par Spring Security
import sn.yooni.service.LivreurService; // Service métier des livreurs

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Injection du principal authentifié
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.PathVariable; // Liaison des paramètres d'URL (ex: /1)
import org.springframework.web.bind.annotation.PutMapping; // Mappage des requêtes PUT
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de profils

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/livreurs") // Gestion des profils livreurs
@RequiredArgsConstructor // Constructeur généré pour l'injection de LivreurService
@PreAuthorize("isAuthenticated()") // Toutes les routes exigent une authentification
public class LivreurController { // Exposition des endpoints de gestion des livreurs

    private final LivreurService livreurService; // Service métier des livreurs

    // GET /api/livreurs/me — profil du livreur authentifié
    @GetMapping("/me")
    public ResponseEntity<LivreurDTO> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(livreurService.getProfile(principal.getId())); // Profil du compte connecté
    }

    // GET /api/livreurs/disponibles — liste des livreurs disponibles pour une course
    @GetMapping("/disponibles")
    public ResponseEntity<List<LivreurDTO>> disponibles() {
        return ResponseEntity.ok(livreurService.listDisponibles()); // Livreurs en ligne et libres
    }

    // GET /api/livreurs/{id} — détail d'un profil livreur
    @GetMapping("/{id}")
    public ResponseEntity<LivreurDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(livreurService.getLivreur(id)); // Détail du profil demandé
    }

    // PUT /api/livreurs/{id}/disponibilite — bascule en ligne / hors ligne
    @PutMapping("/{id}/disponibilite")
    @PreAuthorize("hasAnyRole('LIVREUR','ADMIN')") // Le livreur lui-même ou un administrateur
    public ResponseEntity<LivreurDTO> toggleDisponibilite(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserPrincipal principal) {
        // L'identité est transmise au service : il refuse la bascule si le profil {id}
        // n'appartient ni au demandeur ni à un administrateur (anti-IDOR).
        return ResponseEntity.ok(livreurService.toggleDisponibilite(principal.getId(), id));
    }

}