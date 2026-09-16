package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import sn.yooni.config.JwtProperties; // Importation des propriétés JWT liées à la configuration
import sn.yooni.model.User; // Importation de l'entité User

import io.jsonwebtoken.Claims; // Revendications (claims) contenues dans le jeton
import io.jsonwebtoken.Jwts; // API de construction et d'analyse des jetons JJWT
import io.jsonwebtoken.security.Keys; // Fabrique de clés HMAC à partir de la clé secrète

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.stereotype.Service; // Stéréotype service métier

import javax.crypto.SecretKey; // Clé secrète HMAC SHA-256 utilisée pour signer les jetons
import java.nio.charset.StandardCharsets; // Encodage UTF-8 de la clé secrète
import java.util.Date; // Horodatage d'émission et d'expiration du jeton
import java.util.UUID; // Identifiant unique (jti) inséré dans chaque jeton

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection de JwtProperties
public class JwtTokenProvider { // Fournisseur central de création et de validation des jetons JWT

    private final JwtProperties jwtProperties; // Configuration : clé secrète et durées de validité

    // Déduit la clé HMAC SHA-256 à partir de la clé secrète configurée (>= 32 octets)
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.jwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Génère un jeton d'accès court (24h) à partir de l'entité User
    public String generateAccessToken(User user) {
        return buildToken(user, jwtProperties.jwtExpirationMs());
    }

    // Génère un jeton de rafraîchissement long (7 jours) à partir de l'entité User
    public String generateRefreshToken(User user) {
        return buildToken(user, jwtProperties.jwtRefreshExpirationMs());
    }

    // Construit un jeton signé en HS256 contenant l'e-mail (subject), le rôle et l'id en claims.
    // Un identifiant jti (UUID) est inséré : garantit que DEUX jetons ne sont jamais identiques,
    // même émis la même seconde, et donne une base pour une éventuelle révocation individuelle.
    private String buildToken(User user, long expirationMs) {
        Date now = new Date(); // Instant courant
        Date expiration = new Date(now.getTime() + expirationMs); // Instant d'expiration calculé
        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // jti unique : deux jetons identiques sont impossibles
                .subject(user.getEmail()) // Identifiant stable du sujet du jeton
                .claim("role", user.getRole().name()) // Rôle injecté dans les claims
                .claim("uid", user.getId()) // Identifiant technique injecté dans les claims
                .issuedAt(now) // Date d'émission
                .expiration(expiration) // Date d'expiration
                .signWith(getSigningKey()) // Signature HMAC SHA-256
                .compact(); // Sérialisation finale en chaîne compacte
    }

    // Extrait l'e-mail (subject) d'un jeton sans vérifier sa validité globale
    public String extractEmail(String token) {
        return parseClaims(token).getSubject(); // Récupère le subject du payload
    }

    // Extrait l'identifiant technique (claim "uid") d'un jeton validé.
    // Utilisé notamment pour associer une session WebSocket au bon utilisateur.
    public Long extractUserId(String token) {
        Object uid = parseClaims(token).get("uid"); // Claim entier injecté à la génération
        return uid instanceof Number ? ((Number) uid).longValue() : null; // Conversion sûre
    }

    // Vérifie que le jeton est bien signé, non invalide et non expiré
    public boolean isValid(String token) {
        try {
            Claims claims = parseClaims(token); // Parse du jeton (échec si signature invalide)
            return claims.getExpiration().after(new Date()); // Vérifie la non-expiration
        } catch (Exception e) {
            return false; // Tout jeton falsifié, expiré ou illisible est rejeté
        }
    }

    // Parse et vérifie la signature du jeton (jette une exception si invalide)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Vérifie la signature avec la clé configurée
                .build()
                .parseSignedClaims(token) // Analyse le jeton et retourne ses claims
                .getPayload(); // Récupère uniquement le payload signé
    }

    // Calcule la date d'expiration d'un RefreshToken à partir de la durée configurée
    public Date computeRefreshExpiration() {
        long now = System.currentTimeMillis(); // Instant courant en millisecondes
        return new Date(now + jwtProperties.jwtRefreshExpirationMs()); // Expiration = maintenant + 7 jours
    }

}