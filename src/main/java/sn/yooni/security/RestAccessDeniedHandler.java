package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import com.fasterxml.jackson.databind.ObjectMapper; // Sérialisation JSON de la réponse d'erreur

import jakarta.servlet.http.HttpServletRequest; // Requête HTTP entrante
import jakarta.servlet.http.HttpServletResponse; // Réponse HTTP sortante

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour le champ ObjectMapper

import org.springframework.http.MediaType; // Type de contenu JSON dans la réponse
import org.springframework.security.access.AccessDeniedException; // Exception d'accès refusé (403)
import org.springframework.security.web.access.AccessDeniedHandler; // Contrat de réponse aux refus d'accès
import org.springframework.stereotype.Component; // Enregistrement du composant comme bean Spring

import java.io.IOException; // Exception d'entrée/sortie
import java.time.Instant; // Horodatage UTC de l'erreur
import java.util.Map; // Corps JSON de l'erreur

@Component // Bean enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection d'ObjectMapper
public class RestAccessDeniedHandler implements AccessDeniedHandler { // Réponse 403 JSON aux droits insuffisants

    private final ObjectMapper objectMapper; // Sérialiseur JSON

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        // Construit un corps JSON cohérent et non détaillé
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(), // Date UTC de l'erreur
                "status", HttpServletResponse.SC_FORBIDDEN, // Code HTTP 403
                "error", "Forbidden", // Libellé court du code
                "message", "Accès refusé : vous ne disposez pas des droits nécessaires.", // Message lisible
                "path", request.getRequestURI() // Route qui a déclenché l'erreur
        );
        // Configure la réponse : statut 403 et contenu JSON
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        // Écrit le corps JSON dans la réponse
        objectMapper.writeValue(response.getWriter(), body);
    }

}