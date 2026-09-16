package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.PaiementDTO; // DTO de réponse pour les paiements
import sn.yooni.dto.request.InitierPaiementRequest; // Données de la demande de paiement
import sn.yooni.dto.response.MessageResponse; // Réponse générique
import sn.yooni.security.UserPrincipal; // Utilisateur authentifié fourni par Spring Security
import sn.yooni.service.PaiementService; // Service métier des paiements

import jakarta.validation.Valid; // Déclenche la validation Bean Validation sur le corps JSON

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Injection du principal authentifié
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.PathVariable; // Liaison des paramètres d'URL (ex: /1)
import org.springframework.web.bind.annotation.PostMapping; // Mappage des requêtes POST
import org.springframework.web.bind.annotation.RequestBody; // Lecture du corps JSON de la requête
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de paiements

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/paiements") // Gestion des paiements
@PreAuthorize("isAuthenticated()") // Toutes les routes exigent une authentification
@RequiredArgsConstructor // Constructeur généré pour l'injection de PaiementService
public class PaiementController { // Exposition des endpoints de gestion des paiements

    private final PaiementService paiementService; // Service métier des paiements

    // POST /api/paiements — initier le paiement d'une livraison terminée (CLIENT propriétaire)
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')") // Seul un client peut régler une course
    public ResponseEntity<PaiementDTO> initier(@Valid @RequestBody InitierPaiementRequest request,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        PaiementDTO cree = paiementService.initier(principal.getId(), request); // Enregistre le paiement
        return ResponseEntity.status(201).body(cree); // Réponse 201 Created avec les détails du paiement
    }

    // GET /api/paiements/livraison/{livraisonId} — consulter le paiement d'une livraison (propriétaire ou ADMIN)
    @GetMapping("/livraison/{livraisonId}")
    public ResponseEntity<PaiementDTO> paiementLivraison(@PathVariable Long livraisonId,
                                                          @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(paiementService.paiementLivraison(principal.getId(), livraisonId)); // Détails
    }

    // GET /api/paiements — liste de tous les paiements (réservée à l'administration)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Seul un administrateur peut consulter tous les paiements
    public ResponseEntity<List<PaiementDTO>> liste() {
        return ResponseEntity.ok(paiementService.liste()); // Liste complète
    }

}