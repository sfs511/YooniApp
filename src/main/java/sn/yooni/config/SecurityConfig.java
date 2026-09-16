package sn.yooni.config; // Déclaration du package config dédié aux beans de configuration

import sn.yooni.security.CustomUserDetailsService; // Service de chargement des comptes
import sn.yooni.security.JwtAuthenticationEntryPoint; // Réponse 401 JSON
import sn.yooni.security.JwtAuthenticationFilter; // Filtre d'authentification JWT
import sn.yooni.security.RateLimitingFilter; // Filtre anti-brute-force (Bucket4j)
import sn.yooni.security.RestAccessDeniedHandler; // Réponse 403 JSON

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.context.annotation.Bean; // Déclaration d'un bean de configuration
import org.springframework.context.annotation.Configuration; // Classe de configuration Spring
import org.springframework.http.HttpMethod; // Enumération des méthodes HTTP pour les règles d'accès
import org.springframework.security.authentication.AuthenticationManager; // Gestionnaire d'authentification
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Fournisseur DB + BCrypt
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Source de l'AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Active @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Constructeur de la chaîne de filtres
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Active la sécurité Web
import org.springframework.security.config.http.SessionCreationPolicy; // Politique de session (stateless)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Hachage BCrypt des mots de passe
import org.springframework.security.crypto.password.PasswordEncoder; // Interface commune de hachage des mots de passe
import org.springframework.security.web.SecurityFilterChain; // Chaîne de filtres de sécurité Spring
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Position du filtre JWT dans la chaîne

@Configuration // Classe de configuration Spring
@EnableWebSecurity // Active la sécurité Web de Spring Security
@EnableMethodSecurity // Active la sécurité au niveau des méthodes (@PreAuthorize, @Secured)
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépendances
public class SecurityConfig { // Configuration centrale de la sécurité HTTP

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Filtre d'authentification JWT
    private final RateLimitingFilter rateLimitingFilter; // Filtre anti-brute-force
    private final CustomUserDetailsService userDetailsService; // Service de chargement des comptes
    private final JwtAuthenticationEntryPoint authenticationEntryPoint; // Réponse 401 JSON
    private final RestAccessDeniedHandler accessDeniedHandler; // Réponse 403 JSON

    // Définit la chaîne de filtres de sécurité appliquée à toutes les requêtes HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API stateless : CSRF inutile car aucun cookie de session n'est utilisé
                .csrf(csrf -> csrf.disable())
                // Active la configuration CORS déclarée dans CorsConfig
                .cors(cors -> {})
                // Sessions sans état : chaque requête est authentifiée indépendamment via JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Règles d'accès selon l'URL
                .authorizeHttpRequests(auth -> auth
                        // Routes d'authentification publiques (register, login, refresh, logout)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Pages et ressources statiques publiques uniquement
                        // (les répertoires /admin/**, /client/**, /livreur/** ne sont PAS publics :
                        // ils devront être protégés car ils contiendront des données utilisateurs)
                        .requestMatchers("/", "/index.html", "/login.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        // Preflight CORS (OPTIONS) toujours accepté
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Toute autre route exige un utilisateur authentifié
                        .anyRequest().authenticated())
                // Gestion des erreurs d'authentification (401) et d'autorisation (403) en JSON
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // Réponse 401 JSON
                        .accessDeniedHandler(accessDeniedHandler)) // Réponse 403 JSON
                // Fournisseur d'authentification basé sur la base de données + BCrypt
                .authenticationProvider(authenticationProvider())
                // Ordre des filtres : Rate Limiting → JWT → UsernamePasswordAuthentication.
                // L'ancrage se fait sur un filtre standard de Spring Security (UsernamePasswordAuthenticationFilter),
                // car uniquement les filtres du référentiel Spring Security ont un ordre enregistré.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        // Construit et retourne la chaîne de filtres finale
        return http.build();
    }

    // Fournisseur d'authentification : charge le compte depuis la BDD et vérifie le BCrypt
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // Instancie le fournisseur
        provider.setUserDetailsService(userDetailsService); // Source des comptes (UserRepository)
        provider.setPasswordEncoder(passwordEncoder()); // Comparaison en BCrypt
        return provider; // Retourne le fournisseur configuré
    }

    // Encodeur de mots de passe : BCrypt (sel fort, résistant aux attaques par dictionnaire)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Standard de l'industrie pour le hachage des mots de passe
    }

    // Gestionnaire d'authentification exposé aux services (utilisé par AuthService pour le login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager(); // Construit l'AuthenticationManager à partir de la config
    }

}