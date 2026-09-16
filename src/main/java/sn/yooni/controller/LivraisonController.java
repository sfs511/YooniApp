package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.LivraisonDTO; // DTO de réponse pour les livraisons
import sn.yooni.dto.request.CreerLivraisonRequest; // Données de la demande de course
import sn.yooni.dto.response.MessageResponse; // Réponse générique
import sn.yooni.security.UserPrincipal; // Utilisateur authentifié fourni par Spring Security
import sn.yooni.service.LivraisonService; // Service métier des livraisons

import jakarta.validation.Valid; // Déclenche la validation Bean Validation sur le corps JSON

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Injection du principal authentifié
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.PathVariable; // Liaison des paramètres d'URL (ex: /1)
import org.springframework.web.bind.annotation.PostMapping; // Mappage des requêtes POST
import org.springframework.web.bind.annotation.PutMapping; // Mappage des requêtes PUT
import org.springframework.web.bind.annotation.RequestBody; // Lecture du corps JSON de la requête
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de livraisons

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/livraisons") // Gestion des courses et livraisons
@PreAuthorize("isAuthenticated()") // Toutes les routes exigent une authentification
@RequiredArgsConstructor // Constructeur généré pour l'injection de LivraisonService
public class LivraisonController { // Exposition des endpoints de gestion des livraisons

    private final LivraisonService livraisonService; // Service métier des livraisons

    // POST /api/livraisons — création d'une demande de course (CLIENT)
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')") // Seul un client peut demander une course
    public ResponseEntity<LivraisonDTO> creer(@Valid @RequestBody CreerLivraisonRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        LivraisonDTO creee = livraisonService.creer(principal.getId(), request); // Crée la demande
        return ResponseEntity.status(201).body(creee); // Réponse 201 Created avec la course créée
    }

    // GET /api/livraisons/mes — historique des courses du client ou du livreur courant
    @GetMapping("/mes")
    public ResponseEntity<List<LivraisonDTO>> mesLivraisons(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(livraisonService.mesLivraisons(principal.getId())); // Historique filtré par rôle
    }

    // GET /api/livraisons/{id} — détail d'une livraison (client propriétaire, livreur assigné ou ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> get(@PathVariable Long id,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        // L'identité du demandeur est transmise au service : il applique le contrôle
        // de propriété (anti-IDOR) avant de retourner le moindre détail de la course.
        return ResponseEntity.ok(livraisonService.getLivraison(principal.getId(), id));
    }

    // PUT /api/livraisons/{id}/accepter — le livreur prend une course en charge (LIVREUR)
    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('LIVREUR')") // Seul un livreur peut accepter un service
    public ResponseEntity<LivraisonDTO> accepter(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        LivraisonDTO acceptee = livraisonService.accepter(principal.getId(), id); // Acceptation de la course
        return ResponseEntity.ok(acceptee); // Réponse 200 avec la course à l'état ACCEPTEE
    }

    // PUT /api/livraisons/{id}/terminer — le livreur clôture la course (LIVREUR)
    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasRole('LIVREUR')") // Seul le livreur assigné peut clôturer
    public ResponseEntity<LivraisonDTO> terminer(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        LivraisonDTO terminee = livraisonService.terminer(principal.getId(), id); // Clôture de la course
        return ResponseEntity.ok(terminee); // Réponse 200 avec la course à l'état TERMINEE
    }

    // PUT /api/livraisons/{id}/annuler — annulation de la course (CLIENT ou ADMIN)
    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')") // Le client propriétaire ou un administrateur
    public ResponseEntity<MessageResponse> annuler(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        livraisonService.annuler(principal.getId(), id); // Annulation de la course
        return ResponseEntity.ok(new MessageResponse("Course annulée avec succès.")); // Réponse de confirmation
    }

}