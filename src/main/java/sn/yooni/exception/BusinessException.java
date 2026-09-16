package sn.yooni.exception; // Déclaration du package exception (gestion des erreurs applicatives)

import org.springframework.http.HttpStatus; // Codes de statut HTTP

import lombok.Getter; // Génération du getter pour le statut HTTP

// Exception métier transportant un code HTTP : permet aux services de lever des erreurs précises et lisibles
@Getter // Génère automatiquement getHttpStatus() et getMessage()
public class BusinessException extends RuntimeException { // Exception contrôlée de la couche service

    private final HttpStatus status; // Code HTTP à retourner (409, 401, 404...)

    // Constructeur : associe un message d'erreur à un statut HTTP
    public BusinessException(HttpStatus status, String message) {
        super(message); // Transmet le message à l'exception de base
        this.status = status; // Conserve le statut HTTP
    }

}