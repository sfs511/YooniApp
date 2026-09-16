package sn.yooni.config; // Déclaration du package config dédié aux beans de configuration

import org.springframework.beans.factory.annotation.Value; // Lecture des propriétés d'application
import org.springframework.context.annotation.Bean; // Déclaration d'un bean de configuration
import org.springframework.context.annotation.Configuration; // Classe de configuration Spring
import org.springframework.web.cors.CorsConfiguration; // Configuration CORS
import org.springframework.web.cors.CorsConfigurationSource; // Source de la configuration CORS
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Enregistrement des règles CORS par chemin

import java.util.List; // Liste des origines/méthodes autorisées

@Configuration // Classe de configuration Spring
public class CorsConfig { // Configuration CORS centralisée (cross-origin pour les clients web/mobiles)

    // Origines autorisées, définies via la variable d'environnement CORS_ALLOWED_ORIGINS.
    // Le défaut ne couvre QUE le développement local (même hôte, pages statiques servies par Spring).
    // ⚠️ En production : renseigner obligatoirement les domaines réels du front (ex : https://app.yooni.sn)
    // au lieu de "*" — un CORS ouvert à toutes les origines permet à tout site malveillant
    // d'appeler l'API avec les jetons de vos utilisateurs.
    @Value("${yooni.app.cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080}")
    private List<String> allowedOrigins;

    // Déclare la source de configuration CORS utilisée par Spring Security et Spring MVC
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration(); // Instancie la configuration
        configuration.setAllowedOrigins(allowedOrigins); // Origines restreintes et configurables
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // Méthodes autorisées
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept")); // En-têtes nécessaires uniquement
        // NOTE : allowCredentials reste false (défaut) — l'authentification se fait par
        // jeton JWT dans l'en-tête Authorization, jamais par cookie de session.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Source par URL
        source.registerCorsConfiguration("/**", configuration); // Applique la règle à toutes les routes
        return source; // Retourne la source CORS configurée
    }

}