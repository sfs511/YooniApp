package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.ClientDTO; // DTO de réponse pour les profils clients
import sn.yooni.security.UserPrincipal; // Utilisateur authentifié fourni par Spring Security
import sn.yooni.service.ClientService; // Service métier des clients

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Injection du principal authentifié
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de profils

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/clients") // Gestion des profils clients
@RequiredArgsConstructor // Constructeur généré pour l'injection de ClientService
public class ClientController { // Exposition des endpoints de gestion des clients

    private final ClientService clientService; // Service métier des clients

    // GET /api/clients/me — profil du client authentifié
    @GetMapping("/me")
    public ResponseEntity<ClientDTO> me(@AuthenticationPrincipal UserPrincipal principal) {
        ClientDTO profile = clientService.getProfile(principal.getId()); // Profil du compte connecté
        return ResponseEntity.ok(profile); // Réponse 200 avec le profil
    }

    // GET /api/clients — liste des clients (réservée à l'administration)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Seul un administrateur peut lister tous les clients
    public ResponseEntity<List<ClientDTO>> list() {
        return ResponseEntity.ok(clientService.listAll()); // Réponse 200 avec la liste complète
    }

}