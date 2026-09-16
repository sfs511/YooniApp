package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import com.fasterxml.jackson.databind.ObjectMapper; // Sérialisation JSON de la réponse d'erreur

import jakarta.servlet.http.HttpServletRequest; // Requête HTTP entrante
import jakarta.servlet.http.HttpServletResponse; // Réponse HTTP sortante

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour le champ ObjectMapper

import org.springframework.http.MediaType; // Type de contenu JSON dans la réponse
import org.springframework.security.core.AuthenticationException; // Exception d'authentification
import org.springframework.security.web.AuthenticationEntryPoint; // Contrat de réponse aux accès non authentifiés
import org.springframework.stereotype.Component; // Enregistrement du composant comme bean Spring

import java.io.IOException; // Exception d'entrée/sortie
import java.time.Instant; // Horodatage UTC de l'erreur
import java.util.Map; // Corps JSON de l'erreur

@Component // Bean enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection d'ObjectMapper
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint { // Réponse 401 JSON aux accès non authentifiés

    private final ObjectMapper objectMapper; // Sérialiseur JSON de Spring ou Jackson

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Construit un corps JSON cohérent et non détaillé (pas d'information technique divulguée)
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(), // Date UTC de l'erreur
                "status", HttpServletResponse.SC_UNAUTHORIZED, // Code HTTP 401
                "error", "Unauthorized", // Libellé court du code
                "message", "Authentification requise : jeton manquant, invalide ou expiré.", // Message lisible
                "path", request.getRequestURI() // Route qui a déclenché l'erreur
        );
        // Configure la réponse : statut 401 et contenu JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        // Écrit le corps JSON dans la réponse
        objectMapper.writeValue(response.getWriter(), body);
    }

}