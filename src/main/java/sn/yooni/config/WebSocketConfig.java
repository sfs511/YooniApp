package sn.yooni.config; // Déclaration du package config dédié aux beans de configuration

import sn.yooni.websocket.JwtHandshakeInterceptor; // Validation du jeton au handshake
import sn.yooni.websocket.NotificationWebSocketHandler; // Gestionnaire des notifications temps réel
import sn.yooni.websocket.TrackingWebSocketHandler; // Gestionnaire du suivi GPS temps réel

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.beans.factory.annotation.Value; // Lecture des propriétés d'application
import org.springframework.context.annotation.Configuration; // Classe de configuration Spring
import org.springframework.web.socket.config.annotation.EnableWebSocket; // Active le support WebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer; // Contrat d'enregistrement des handlers
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry; // Registre des handlers WebSocket

import java.util.List; // Liste des origines autorisées

@Configuration // Classe de configuration Spring
@EnableWebSocket // Active le protocole WebSocket dans Spring MVC
@RequiredArgsConstructor // Constructeur généré pour l'injection des handlers
public class WebSocketConfig implements WebSocketConfigurer { // Configuration des endpoints WebSocket temps réel

    private final NotificationWebSocketHandler notificationWebSocketHandler; // Diffusion des notifications
    private final TrackingWebSocketHandler trackingWebSocketHandler; // Diffusion des positions GPS
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor; // Authentification JWT des connexions

    // Origines autorisées pour le handshake WebSocket (défault : développement local).
    // En production, définir CORS_ALLOWED_ORIGINS avec les vrais domaines du front.
    @Value("${yooni.app.cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080}")
    private List<String> allowedOrigins;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Endpoint /ws/notifications : notifications push vers le client connecté.
        // Le handshake est authentifié via le JWT (voir JwtHandshakeInterceptor).
        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
                .addInterceptors(jwtHandshakeInterceptor) // Refuse les connexions sans jeton valide
                .setAllowedOrigins(allowedOrigins.toArray(new String[0])); // Origines limitées et configurables
        // Endpoint /ws/tracking : mise à jour temps réel des positions des livreurs
        registry.addHandler(trackingWebSocketHandler, "/ws/tracking")
                .addInterceptors(jwtHandshakeInterceptor) // Même authentification obligatoire
                .setAllowedOrigins(allowedOrigins.toArray(new String[0]));
    }

}