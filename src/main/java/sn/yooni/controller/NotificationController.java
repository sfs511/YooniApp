package sn.yooni.controller; // Déclaration du package controller (couche d'exposition REST)

import sn.yooni.dto.NotificationDTO; // DTO de réponse pour les notifications
import sn.yooni.dto.response.MessageResponse; // Réponse générique
import sn.yooni.security.UserPrincipal; // Utilisateur authentifié fourni par Spring Security
import sn.yooni.service.NotificationService; // Service métier des notifications

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.security.access.prepost.PreAuthorize; // Sécurité au niveau des méthodes
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Injection du principal authentifié
import org.springframework.web.bind.annotation.GetMapping; // Mappage des requêtes GET
import org.springframework.web.bind.annotation.PathVariable; // Liaison des paramètres d'URL (ex: /1)
import org.springframework.web.bind.annotation.PutMapping; // Mappage des requêtes PUT
import org.springframework.web.bind.annotation.RequestMapping; // Préfixe commun des routes
import org.springframework.web.bind.annotation.RestController; // Contrôleur REST

import java.util.List; // Liste de notifications

@RestController // Contrôleur REST : réponse JSON automatique
@RequestMapping("/api/notifications") // Notifications utilisateur
@PreAuthorize("isAuthenticated()") // Toutes les routes exigent une authentification
@RequiredArgsConstructor // Constructeur généré pour l'injection de NotificationService
public class NotificationController { // Exposition des endpoints de gestion des notifications

    private final NotificationService notificationService; // Service métier des notifications

    // GET /api/notifications — toutes les notifications du compte courant
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> mesNotifications(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.mesNotifications(principal.getId())); // File complète du compte
    }

    // GET /api/notifications/non-lues — notifications non lues du compte courant
    @GetMapping("/non-lues")
    public ResponseEntity<List<NotificationDTO>> nonLues(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.nonLues(principal.getId())); // Alertes en attente de lecture
    }

    // PUT /api/notifications/{id}/lue — marquer une notification comme lue
    @PutMapping("/{id}/lue")
    public ResponseEntity<MessageResponse> marquerLue(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.marquerLue(principal.getId(), id); // Marquage de la notification
        return ResponseEntity.ok(new MessageResponse("Notification marquée comme lue.")); // Confirmation
    }

}