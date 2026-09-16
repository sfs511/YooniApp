package sn.yooni.websocket; // Déclaration du package websocket (communication temps réel)

import org.springframework.context.event.EventListener; // Écouteur d'événements Spring
import org.springframework.stereotype.Component; // Enregistrement du composant comme bean Spring
import org.springframework.web.socket.messaging.SessionConnectedEvent; // Événement de connexion WebSocket
import org.springframework.web.socket.messaging.SessionDisconnectEvent; // Événement de déconnexion WebSocket

import java.util.logging.Logger; // Logger simple pour tracer les événements

@Component // Bean enregistré dans le conteneur Spring
public class WebSocketEventListener { // Écouteur des événements de connexion/déconnexion WebSocket

    private static final Logger logger = Logger.getLogger(WebSocketEventListener.class.getName());

    // Traite les événements de connexion : log l'identifiant de la session et l'origine
    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId", String.class); // ID de session
        logger.info("WebSocket session connectée : " + sessionId); // Log de connexion
    }

    // Traite les événements de déconnexion : log l'identifiant de la session et le motif
    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId(); // ID de session déconnectée
        logger.info("WebSocket session déconnectée : " + sessionId); // Log de déconnexion
    }

}