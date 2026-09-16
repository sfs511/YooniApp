package sn.yooni.exception; // Déclaration du package exception (gestion des erreurs applicatives)

import jakarta.validation.ConstraintViolationException; // Violations de contraintes de validation

import org.springframework.dao.OptimisticLockingFailureException; // Conflit de concurrence JPA
import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.http.ResponseEntity; // Réponse HTTP construite
import org.springframework.http.converter.HttpMessageNotReadableException; // Corps JSON illisible
import org.springframework.security.access.AccessDeniedException; // Accès refusé (droits insuffisants)
import org.springframework.security.authentication.DisabledException; // Compte utilisateur désactivé
import org.springframework.security.core.AuthenticationException; // Échec d'authentification (BCrypt)
import org.springframework.web.bind.MethodArgumentNotValidException; // Échec de validation @Valid d'un corps
import org.springframework.web.bind.annotation.ExceptionHandler; // Liaison exception -> méthode de gestion
import org.springframework.web.bind.annotation.RestControllerAdvice; // Gestion globale des exceptions REST
import org.springframework.web.servlet.resource.NoResourceFoundException; // Route/intégration introuvable

import lombok.extern.slf4j.Slf4j; // Journalisation des erreurs

import java.time.Instant; // Horodatage UTC des erreurs
import java.util.LinkedHashMap; // Map ordonnée des erreurs de champ
import java.util.Map; // Map des erreurs de champ

@Slf4j // Génère un logger SLF4J pour journaliser les erreurs internes
@RestControllerAdvice // Intercepte toutes les exceptions levées par les contrôleurs REST
public class GlobalExceptionHandler { // Gestionnaire centralisé et normalisé des erreurs de l'API

    // Erreurs de validation @Valid (@NotBlank, @Email, @Size...) → 400 avec le détail par champ
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>(); // Conserve l'ordre des erreurs
        // Agrège chaque erreur de champ : nom du champ -> message de violation
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation des données échouée.", request, fieldErrors);
    }

    // Violation de contrainte (validations au niveau des paramètres) → 400
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    // Corps JSON illisible ou champ inconnu → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex,
                                                         jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Corps de la requête JSON mal formé.", request, null);
    }

    // Mauvais identifiants lors du login → 401
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex,
                                                         jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "E-mail ou mot de passe incorrect.", request, null);
    }

    // Compte désactivé (actif=false) lors de la connexion → 403, message distinct :
    // l'utilisateur sait que son compte existe mais est inactif (vérification propre)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(DisabledException ex,
                                                   jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Votre compte est désactivé. Contactez l'administration.", request, null);
    }

    // Conflit de concurrence JPA (ex : acceptation simultanée d'une même course) → 409
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(OptimisticLockingFailureException ex,
                                                         jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflit de modification simultanée. Veuillez réessayer.", request, null);
    }

    // Accès refusé par les règles d'autorisation @PreAuthorize → 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                       jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Accès refusé : droits insuffisants.", request, null);
    }

    // Resource ou route introuvable → 404
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NoResourceFoundException ex,
                                                   jakarta.servlet.http.HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Ressource introuvable.", request, null);
    }

    // Exception métier portant son propre code HTTP (conflit, non trouvé, etc.)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex,
                                                   jakarta.servlet.http.HttpServletRequest request) {
        return build(ex.getStatus(), ex.getMessage(), request, null);
    }

    // Filet de sécurité : toute exception non prévue → 500 (journalisée, jamais détaillée au client)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex,
                                                    jakarta.servlet.http.HttpServletRequest request) {
        log.error("Erreur interne non gérée sur {}", request.getRequestURI(), ex); // Trace complète côté serveur
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue.", request, null);
    }

    // Construit une réponse ApiError normalisée à partir du statut, du message et des erreurs de champ
    private ResponseEntity<ApiError> build(HttpStatus status, String message,
                                           jakarta.servlet.http.HttpServletRequest request,
                                           Map<String, String> fieldErrors) {
        ApiError apiError = new ApiError(
                Instant.now(), // Horodatage de l'erreur
                status.value(), // Code HTTP numérique
                status.getReasonPhrase(), // Libellé du code (ex: "Bad Request")
                message, // Message lisible
                request.getRequestURI(), // Route concernée
                fieldErrors // Détail des erreurs de validation (ou null)
        );
        return ResponseEntity.status(status).body(apiError); // Réponse HTTP avec le corps JSON normalisé
    }

}