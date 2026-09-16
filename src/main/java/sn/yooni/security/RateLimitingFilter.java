package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import com.fasterxml.jackson.databind.ObjectMapper; // Sérialisation JSON de la réponse 429

import io.github.bucket4j.Bandwidth; // Limite de débit (requêtes par période)
import io.github.bucket4j.Bucket; // Seau à jetons (token bucket)
import io.github.bucket4j.Refill; // Règle de reconstitution des jetons

import jakarta.servlet.FilterChain; // Chaîne des filtres Servlet à enchaîner
import jakarta.servlet.ServletException; // Exception levée par le conteneur Servlet
import jakarta.servlet.http.HttpServletRequest; // Requête HTTP entrante
import jakarta.servlet.http.HttpServletResponse; // Réponse HTTP sortante

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.beans.factory.annotation.Value; // Lecture de la propriété de confiance du proxy
import org.springframework.http.MediaType; // Type de contenu JSON dans la réponse
import org.springframework.lang.NonNull; // Annotation marquant un paramètre non nul
import org.springframework.stereotype.Component; // Enregistrement du filtre comme bean Spring
import org.springframework.web.filter.OncePerRequestFilter; // Filtre garantissant une seule exécution par requête

import java.io.IOException; // Exception d'entrée/sortie
import java.time.Duration; // Période de référence du débit
import java.time.Instant; // Horodatage UTC de la réponse 429
import java.util.Map; // Corps JSON de l'erreur
import java.util.concurrent.ConcurrentHashMap; // Table de buckets thread-safe par adresse IP

@Component // Bean enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection d'ObjectMapper
public class RateLimitingFilter extends OncePerRequestFilter { // Filtre anti-brute-force basé sur Bucket4j

    // Budget de débit : 60 requêtes par minute et par adresse IP
    private static final int REQUEST_LIMIT = 60; // Nombre de requêtes autorisées
    private static final Duration PERIOD = Duration.ofMinutes(1); // Période de référence (1 minute)

    // Garde-fou mémoire : ne conserver au maximum que 10 000 buckets d'IP distinctes.
    // Sans limite, un attaquant générant des identifiants variés pourrait faire grossir
    // la carte indéfiniment (épuisement mémoire = déni de service).
    private static final int MAX_BUCKETS = 10_000;
    // Un bucket inactif depuis plus de 10 minutes est retiré (les IP récurrentes sont recréées au besoin)
    private static final long BUCKET_IDLE_TIMEOUT_MS = Duration.ofMinutes(10).toMillis();

    private final ObjectMapper objectMapper; // Sérialiseur JSON
    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>(); // Un bucket par IP (thread-safe)

    // En production derrière un proxy de confiance (nginx, ALB...), l'en-tête X-Forwarded-For
    // peut être pris en compte. Sinon il est ignoré : un attaquant pourrait le falsifier
    // et ainsi changer d'identité à chaque requête pour contourner la limitation.
    @Value("${yooni.app.rate-limit.trust-forwarded-header:false}")
    private boolean trustForwardedHeader;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Applique la limitation uniquement aux routes API (/api/**), pas aux ressources statiques
        if (request.getRequestURI().startsWith("/api/")) {
            String clientIp = resolveClientIp(request); // Adresse IP réelle du client
            BucketEntry entry = getOrCreateBucket(clientIp); // Obtient (ou crée) le bucket de l'IP
            if (!entry.bucket.tryConsume(1)) { // Consomme un jeton : échec si le budget est dépassé
                writeTooManyRequests(response, request, clientIp); // Retourne 429 JSON
                return; // Bloque la requête sans poursuivre la chaîne de filtres
            }
        }
        filterChain.doFilter(request, response); // Budget respecté : poursuit le traitement
    }

    // Construit un seau à jetons : 60 jetons initialement, reconstitués en continu (greedy) de 60 jetons/minute
    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(REQUEST_LIMIT, Refill.greedy(REQUEST_LIMIT, PERIOD));
        return Bucket.builder().addLimit(limit).build(); // Seau à jetons prêt à consommer
    }

    // Récupère le bucket d'une IP en le créant au besoin, et entretient sa date de dernière
    // utilisation. Si le nombre de buckets dépasse la limite, les IP inactives sont purgées.
    private BucketEntry getOrCreateBucket(String clientIp) {
        return buckets.compute(clientIp, (ip, existing) -> {
            BucketEntry entry = existing != null ? existing : new BucketEntry(newBucket()); // Crée si absent
            entry.lastAccessMillis = System.currentTimeMillis(); // Met à jour l'horodatage d'activité
            return entry;
        }).thenCleanupIfNeeded(buckets); // Nettoyage périodique hors du chemin critique de la sérialisation
    }

    // Retrouve l'adresse IP du client en respectant le proxy inverse/load balancer si présent
    private String resolveClientIp(HttpServletRequest request) {
        if (trustForwardedHeader) { // Uniquement si un proxy de confiance réécrit l'en-tête
            String forwarded = request.getHeader("X-Forwarded-For"); // Renseigné par les proxies inverses
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim(); // Première IP de la chaîne = client d'origine
            }
        }
        return request.getRemoteAddr(); // Repli : IP directe de la socket
    }

    // Écrit une réponse HTTP 429 (Too Many Requests) au format JSON normalisé
    private void writeTooManyRequests(HttpServletResponse response, HttpServletRequest request, String clientIp) throws IOException {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(), // Date UTC de l'erreur
                "status", 429, // Code HTTP Too Many Requests
                "error", "Too Many Requests", // Libellé court du code
                "message", "Trop de requêtes : veuillez patienter avant de réessayer.", // Message lisible
                "path", request.getRequestURI() // Route qui a déclenché l'erreur
        );
        response.setStatus(429); // Statut HTTP 429
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Corps JSON
        response.setCharacterEncoding("UTF-8"); // Encodage UTF-8
        objectMapper.writeValue(response.getWriter(), body); // Sérialise et écrit le corps
    }

    // Enveloppe d'un bucket avec sa date de dernière utilisation (nécessaire pour l'éviction)
    private static final class BucketEntry {

        private final Bucket bucket; // Bucket Bucket4j associé à l'adresse IP
        private volatile long lastAccessMillis; // Dernière requête de cette IP (pour la purge)

        private BucketEntry(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessMillis = System.currentTimeMillis();
        }

        // Purge les buckets inactifs si le nombre de compteurs dépasse le plafond mémoire.
        // Appelée seulement factoriellement (quand le bucket existe déjà) pour ne pas
        // alourdir le traitement de chaque requête.
        private BucketEntry thenCleanupIfNeeded(Map<String, BucketEntry> allBuckets) {
            if (allBuckets.size() > MAX_BUCKETS) {
                long now = System.currentTimeMillis(); // Instant de référence de la purge
                allBuckets.entrySet().removeIf(entry -> now - entry.getValue().lastAccessMillis > BUCKET_IDLE_TIMEOUT_MS);
            }
            return this;
        }
    }

}