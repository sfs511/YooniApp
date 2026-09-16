package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import jakarta.servlet.FilterChain; // Chaîne des filtres Servlet à enchaîner
import jakarta.servlet.ServletException; // Exception levée par le conteneur Servlet
import jakarta.servlet.http.HttpServletRequest; // Requête HTTP entrante
import jakarta.servlet.http.HttpServletResponse; // Réponse HTTP sortante

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.lang.NonNull; // Annotation marquant un paramètre non nul
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Jeton d'authentification
import org.springframework.security.core.context.SecurityContextHolder; // Contexte de sécurité de l'utilisateur courant
import org.springframework.security.core.userdetails.UserDetails; // Utilisateur chargé par le service de détail
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Détails HTTP (IP, session)
import org.springframework.stereotype.Component; // Enregistrement du filtre comme bean Spring
import org.springframework.web.filter.OncePerRequestFilter; // Filtre garantissant une seule exécution par requête

import java.io.IOException; // Exception d'entrée/sortie

@Component // Bean enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépendances
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Filtre d'authentification par jeton JWT

    private final JwtTokenProvider jwtTokenProvider; // Fournisseur de création/validation des jetons
    private final CustomUserDetailsService userDetailsService; // Service chargé de retrouver le compte

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Récupère l'en-tête Authorization (ex: "Bearer eyJh...")
        String authorizationHeader = request.getHeader("Authorization");

        // Analyse le jeton uniquement si l'en-tête est présent et bien formé
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Extrait le jeton sans le préfixe "Bearer "
            try {
                String email = jwtTokenProvider.extractEmail(token); // Lit l'e-mail contenu dans le jeton
                // Authentifie seulement si un e-mail est présent et qu'aucune session n'est déjà établie
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Charge le compte
                    if (jwtTokenProvider.isValid(token)) { // Vérifie signature + expiration
                        // Construit le jeton d'authentification pré-authentifié (le mot de passe n'est pas re-vérifié)
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        // Attache les détails HTTP (adresse IP, session) au jeton d'authentification
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // Injecte l'authentification dans le contexte de sécurité de la requête
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception ignored) {
                // Jeton invalide/expiré : l'utilisateur reste anonyme, le point d'entrée renverra 401 si la route est protégée
            }
        }
        // Poursuit la chaîne de filtres avec l'authentification (ou l'anonymat) déterminée
        filterChain.doFilter(request, response);
    }

}