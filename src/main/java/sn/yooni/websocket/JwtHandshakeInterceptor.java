package sn.yooni.websocket; // Déclaration du package websocket (communication temps réel)

import sn.yooni.security.JwtTokenProvider; // Fournisseur de validation des jetons JWT

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Statut HTTP renvoyé en cas de rejet du handshake
import org.springframework.http.server.ServerHttpRequest; // Requête de handshake WebSocket (abstraite du conteneur)
import org.springframework.http.server.ServerHttpResponse; // Réponse de handshake WebSocket
import org.springframework.http.server.ServletServerHttpRequest; // Implémentation servlet de la requête de handshake
import org.springframework.lang.Nullable; // Annotation marquant un paramètre pouvant être nul
import org.springframework.stereotype.Component; // Enregistrement du composant comme bean Spring
import org.springframework.web.socket.WebSocketHandler; // Gestionnaire WebSocket à authentifier
import org.springframework.web.socket.server.HandshakeInterceptor; // Interception avant/après le handshake

import java.util.Map; // Attributs de session WebSocket partagés avec le handler

/**
 * Intercepteur de handshake WebSocket : refuse toute connexion sans jeton JWT valide.
 *
 * <p>Un navigateur ne peut pas envoyer l'en-tête {@code Authorization} lors d'un
 * handshake WebSocket : le token est donc accepté soit en paramètre d'URL
 * ({@code ?token=...}), soit via l'en-tête {@code Authorization} standard.</p>
 *
 * <p>Le jeton est validé AVANT l'ouverture de la session (signature + expiration),
 * et l'identifiant de l'utilisateur est placé dans les attributs de session.
 * Sans cette étape, n'importe quel client pourrait se connecter aux endpoints
 * /ws/notifications et /ws/tracking et recevoir des données d'autres utilisateurs.</p>
 */
@Component // Bean enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection de JwtTokenProvider
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider; // Validation des jetons JWT

    // Clé sous laquelle l'identifiant utilisateur est rangé dans les attributs de session
    public static final String USER_ID_ATTR = "userId";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = extractToken(request); // Récupère le jeton (header ou paramètre)
        // Aucun jeton ou jeton invalide/expiré : le handshake est rejeté avec un 401
        if (token == null || !jwtTokenProvider.isValid(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
            return false; // Bloque l'ouverture de la session WebSocket
        }
        // Place l'identifiant de l'utilisateur dans les attributs de session :
        // le handler pourra ainsi router les messages vers le bon destinataire.
        attributes.put(USER_ID_ATTR, jwtTokenProvider.extractUserId(token));
        return true; // Accepte la connexion
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               @Nullable Exception exception) {
        // Aucun traitement post-handshake nécessaire (ce comportement est la norme)
    }

    // Extrait le jeton Bearer de l'en-tête Authorization, ou du paramètre d'URL "token"
    // (premier EN-TÊTE, puis repli sur le paramètre pour les clients sans contrôle des en-têtes)
    private String extractToken(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String authorization = servletRequest.getServletRequest().getHeader("Authorization"); // En-tête standard
            if (authorization != null && authorization.startsWith("Bearer ")) {
                return authorization.substring(7); // Retire le préfixe "Bearer "
            }
            String tokenParam = servletRequest.getServletRequest().getParameter("token"); // Paramètre d'URL
            if (tokenParam != null && !tokenParam.isBlank()) {
                return tokenParam; // Jeton fourni par l'application (handshake non-modifiable)
            }
        }
        return null; // Aucun jeton présent
    }

}