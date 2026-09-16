package sn.yooni.websocket; // Déclaration du package websocket (communication temps réel)

import org.springframework.stereotype.Component; // Enregistrement du composant comme bean Spring
import org.springframework.web.socket.CloseStatus; // Statut de fermeture d'une session WebSocket
import org.springframework.web.socket.TextMessage; // Message texte WebSocket
import org.springframework.web.socket.WebSocketSession; // Session WebSocket individuelle
import org.springframework.web.socket.handler.TextWebSocketHandler; // Gestionnaire de messages texte

import java.io.IOException; // Exception d'entrée/sortie lors de l'envoi
import java.util.Map; // Index des sessions par utilisateur
import java.util.Set; // Ensemble de sessions actives
import java.util.concurrent.ConcurrentHashMap; // Tables thread-safe pour les accès concurrents
import java.util.logging.Logger; // Logger simple pour tracer les connexions

/**
 * Diffusion des notifications push vers les seuls destinataires concernés.
 *
 * <p>Les sessions sont indexées par identifiant utilisateur (injecté par
 * {@link JwtHandshakeInterceptor} au moment du handshake) afin que les notifications
 * ne soient PLUS diffusées en broadcast à tous les clients connectés.
 * Avant cette correction, {@code broadcast()} envoyait le message de n'importe quel
 * utilisateur à TOUTES les sessions actives : une notification adressée au client A
 * était lisible par tous les autres clients connectés (fuite de confidentialité).</p>
 */
@Component // Bean enregistré dans le conteneur Spring
public class NotificationWebSocketHandler extends TextWebSocketHandler { // Diffusion des notifications push

    private static final Logger logger = Logger.getLogger(NotificationWebSocketHandler.class.getName());

    // Index : identifiant utilisateur → sessions WebSocket actives de cet utilisateur.
    // Un utilisateur peut être connecté sur plusieurs appareils (tablette + téléphone).
    private final Map<Long, Set<WebSocketSession>> sessionsParUtilisateur = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(JwtHandshakeInterceptor.USER_ID_ATTR); // Identité du client
        // Même si l'intercepteur filtre le handshake, on ne conserve que les sessions authentifiées
        if (userId == null) {
            closeUnAuthenticated(session); // Session anonyme : fermeture immédiate
            return;
        }
        // Ajoute la session au panier de l'utilisateur (créé au besoin, thread-safe)
        sessionsParUtilisateur.computeIfAbsent(userId, id -> ConcurrentHashMap.newKeySet()).add(session);
        logger.info("WebSocket notifications connecté (user " + userId + ") : " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Retire la session de l'index de son utilisateur à la déconnexion
        Long userId = (Long) session.getAttributes().get(JwtHandshakeInterceptor.USER_ID_ATTR);
        if (userId != null) {
            Set<WebSocketSession> userSessions = sessionsParUtilisateur.get(userId);
            if (userSessions != null) {
                userSessions.remove(session); // Retire cette session
                if (userSessions.isEmpty()) {
                    sessionsParUtilisateur.remove(userId); // Purge l'entrée vide
                }
            }
        }
        logger.info("WebSocket notifications déconnecté : " + session.getId());
    }

    // Envoie un message JSON uniquement aux sessions de l'utilisateur ciblé (userTargeted)
    public void sendToUser(Long userId, String jsonMessage) {
        Set<WebSocketSession> userSessions = sessionsParUtilisateur.get(userId); // Sessions du destinataire
        if (userSessions == null) {
            return; // L'utilisateur n'a aucune session WebSocket active
        }
        TextMessage message = new TextMessage(jsonMessage); // Wraps la chaîne en message WebSocket
        for (WebSocketSession session : userSessions) { // Itère sur les sessions du destinataire
            if (session.isOpen()) { // Vérifie que la session est toujours ouverte
                try {
                    session.sendMessage(message); // Envoie le message texte
                } catch (IOException e) {
                    logger.warning("Échec d'envoi de notification via WebSocket : " + e.getMessage());
                }
            }
        }
    }

    // Ferme une session qui n'a pas passé l'authentification (défense en profondeur)
    private void closeUnAuthenticated(WebSocketSession session) {
        try {
            session.close(CloseStatus.POLICY_VIOLATION); // Fermeture avec motif de politique
        } catch (IOException e) {
            logger.warning("Impossible de fermer la session non authentifiée : " + e.getMessage());
        }
    }

}