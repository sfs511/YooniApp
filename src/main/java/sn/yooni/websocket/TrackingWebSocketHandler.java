package sn.yooni.websocket; // Déclaration du package websocket (communication temps réel)

import org.springframework.stereotype.Component; // Enregistrement du composant comme bean Spring
import org.springframework.web.socket.CloseStatus; // Statut de fermeture d'une session WebSocket
import org.springframework.web.socket.TextMessage; // Message texte WebSocket
import org.springframework.web.socket.WebSocketSession; // Session WebSocket individuelle
import org.springframework.web.socket.handler.TextWebSocketHandler; // Gestionnaire de messages texte

import java.io.IOException; // Exception d'entrée/sortie lors de l'envoi
import java.util.Set; // Ensemble de sessions actives
import java.util.concurrent.ConcurrentHashMap; // Table thread-safe pour stocker les sessions
import java.util.logging.Logger; // Logger simple pour tracer les connexions

/**
 * Diffusion des positions GPS temps réel aux clients authentifiés.
 *
 * <p>Connexion réservée aux utilisateurs validés par {@link JwtHandshakeInterceptor} :
 * une session sans identité authentifiée est fermée immédiatement. La diffusion reste
 * un simple broadcast car le suivi de positions est agrégé (carte des livreurs) ;
 * un ciblage par course sera ajouté avec l'implémentation du {@code TrackingService}.</p>
 */
@Component // Bean enregistré dans le conteneur Spring
public class TrackingWebSocketHandler extends TextWebSocketHandler { // Diffusion des positions GPS temps réel

    private static final Logger logger = Logger.getLogger(TrackingWebSocketHandler.class.getName());

    // Ensemble des sessions WebSocket authentifiées (thread-safe pour les accès concurrents)
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet(); // Sessions enregistrées

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(JwtHandshakeInterceptor.USER_ID_ATTR); // Identité du client
        // L'intercepteur a déjà rejeté les handshakes sans jeton valide (défense en profondeur)
        if (userId == null) {
            try {
                session.close(CloseStatus.POLICY_VIOLATION); // Fermeture des sessions anonymes
            } catch (IOException e) {
                logger.warning("Impossible de fermer la session tracking non authentifiée : " + e.getMessage());
            }
            return;
        }
        sessions.add(session); // Enregistre la nouvelle session authentifiée
        logger.info("WebSocket tracking connecté (user " + userId + ") : " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session); // Retire la session à la déconnexion
        logger.info("WebSocket tracking déconnecté : " + session.getId()); // Log de déconnexion
    }

    // Envoie la position GPS à tous les clients authentifiés connectés (broadcast)
    public void broadcast(String jsonPosition) {
        TextMessage message = new TextMessage(jsonPosition); // Wraps la chaîne en message WebSocket
        for (WebSocketSession session : sessions) { // Itère sur toutes les sessions actives
            if (session.isOpen()) { // Vérifie que la session est toujours ouverte
                try {
                    session.sendMessage(message); // Envoie la position
                } catch (IOException e) {
                    logger.warning("Échec d'envoi de position via WebSocket : " + e.getMessage());
                }
            }
        }
    }

}