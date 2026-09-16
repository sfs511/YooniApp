package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.TrackingDTO; // DTO de réponse pour le suivi GPS
import sn.yooni.dto.response.MessageResponse; // Réponse générique

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.PathVariable; // Liaison des paramètres d'URL (ex: /1)
import org.springframework.web.bind.annotation.PostMapping; // Mappage des requêtes POST
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de positions

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/tracking") // Suivi GPS temps réel
@PreAuthorize("isAuthenticated()") // Toutes les routes exigent une authentification
public class TrackingController { // Exposition des endpoints de suivi GPS

    // GET /api/tracking/livreur/{id} — position actuelle d'un livreur
    @GetMapping("/livreur/{id}")
    public ResponseEntity<TrackingDTO> positionLivreur(@PathVariable Long id) {
        // TODO Nouvelle étape : retourner la dernière position via TrackingService
        return ResponseEntity.notFound().build(); // Squelette : 404 tant que le service n'est pas implémenté
    }

    // POST /api/tracking — publication de la position du livreur (autre canal : WebSocket /ws/tracking)
    @PostMapping
    public ResponseEntity<MessageResponse> publierPosition() {
        return ResponseEntity.ok(new MessageResponse("Publication GPS à implémenter.")); // Squelette
    }

    // GET /api/tracking/livreur/{id}/historique — tracé du trajet depuis 1 heure par défaut
    @GetMapping("/livreur/{id}/historique")
    public ResponseEntity<List<TrackingDTO>> historique(@PathVariable Long id) {
        return ResponseEntity.ok(List.of()); // Squelette
    }

}