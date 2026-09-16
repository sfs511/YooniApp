package sn.yooni.config; // Déclaration du package config dédié aux beans de configuration

import org.springframework.boot.context.properties.ConfigurationProperties; // Liaison des propriétés d'application

// Record de configuration lié aux propriétés yooni.app.* via le binding par constructeur.
// NB : enregistré par @EnableConfigurationProperties (voir YooniApplication) — PAS de @Component ici,
// car un record couplé à @Component lève une erreur au démarrage (Spring tenterait d'injecter
// les paramètres String/long comme des beans au lieu de les lier aux propriétés).
@ConfigurationProperties(prefix = "yooni.app") // Lie les propriétés yooni.app.* du fichier application.properties
public record JwtProperties(String jwtSecret, long jwtExpirationMs, long jwtRefreshExpirationMs) {
    // jwtSecret              : clé HMAC SHA-256 (>= 256 bits) définie via ${JWT_SECRET}
    // jwtExpirationMs        : durée de validité du token d'accès (24h par défaut)
    // jwtRefreshExpirationMs : durée de validité du Refresh Token (7 jours par défaut)
}