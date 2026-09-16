package sn.yooni.service; // Déclaration du package service (logique métier)

import org.springframework.stereotype.Service; // Stéréotype service métier

@Service // Bean service enregistré dans le conteneur Spring
public class TrackingService { // Services de suivi GPS temps réel des livreurs

    // TODO Nouvelle étape : injecter PositionRepository puis implémenter :
    // - enregistrement des positions périodiques envoyées par les livreurs
    // - dernière position connue d'un livreur (lecture rapide pour l'affichage carte)
    // - historique horodaté d'un trajet
    // - diffusion des positions via TrackingWebSocketHandler (push WebSocket)
}