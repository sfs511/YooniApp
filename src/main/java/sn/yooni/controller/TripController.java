package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.LivraisonDTO; // DTO des courses VTC (réutilisé pour les trajets)
import sn.yooni.dto.response.MessageResponse; // Réponse générique

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de trajets

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/trips") // Courses VTC (déploiement voiture avec chauffeur)
@PreAuthorize("isAuthenticated()") // Toutes les routes exigent une authentification
public class TripController { // Exposition des endpoints des trajets VTC

    // GET /api/trips — trajets VTC disponibles (moteur d'optimisation)
    @GetMapping("/disponibles")
    public ResponseEntity<List<LivraisonDTO>> tripsDisponibles() {
        // TODO Nouvelle étape : retourner les courses en attente via LivraisonService + OptimisationService
        return ResponseEntity.ok(List.of()); // Liste vide par défaut
    }

    // GET /api/trips/estimer — estimation du prix et de la durée d'un trajet (à venir)
    @GetMapping("/estimer")
    public ResponseEntity<MessageResponse> estimer() {
        return ResponseEntity.ok(new MessageResponse("Estimation VTC à implémenter.")); // Squelette
    }

}