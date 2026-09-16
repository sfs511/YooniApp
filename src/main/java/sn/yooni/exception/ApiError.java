package sn.yooni.exception; // Déclaration du package exception (gestion des erreurs applicatives)

import com.fasterxml.jackson.annotation.JsonInclude; // Exclut les champs nuls du JSON

import java.time.Instant; // Horodatage UTC de l'erreur
import java.util.Map; // Erreurs de validation champ par champ

// Corps JSON normalisé renvoyé en cas d'erreur : structure unique pour toute l'API
@JsonInclude(JsonInclude.Include.NON_NULL) // N'expose pas les attributs nuls (fieldErrors absent si aucune erreur de validation)
public record ApiError(
        Instant timestamp, // Date UTC de survenue de l'erreur
        int status, // Code HTTP (400, 401, 404, 409, 500...)
        String error, // Libellé court du code HTTP (Bad Request, Unauthorized...)
        String message, // Message d'erreur lisible par l'utilisateur
        String path, // Route qui a déclenché l'erreur
        Map<String, String> fieldErrors // Erreurs de validation champ par champ (facultatif)
) { } // Enregistrement immuable sérialisé par Jackson