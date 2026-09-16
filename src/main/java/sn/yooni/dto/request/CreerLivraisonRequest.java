package sn.yooni.dto.request; // Déclaration du package dto.request (données envoyées par le client)

// Validation : la demande doit être complète pour calculer un tarif
import jakarta.validation.constraints.DecimalMax; // Borne supérieure d'une valeur décimale (GPS)
import jakarta.validation.constraints.DecimalMin; // Borne inférieure d'une valeur décimale (GPS)
import jakarta.validation.constraints.NotBlank; // Champ obligatoire non vide
import jakarta.validation.constraints.NotNull; // Champ obligatoire non null
import jakarta.validation.constraints.Size; // Limitation de la longueur d'une chaîne

// Représentation d'une demande de livraison / course VTC envoyée par un client (JSON)
public record CreerLivraisonRequest(
        // Point de départ (lat/lng + libellé) — obligatoire
        @NotBlank(message = "L'adresse de départ est obligatoire.")
        @Size(max = 255, message = "L'adresse de départ ne peut pas dépasser 255 caractères.")
        String adresseDepart, // Nom lisible du point de départ

        // Une latitude hors de [-90 ; 90] et une longitude hors de [-180 ; 180] produiraient
        // des calculs absurdes (distance/tarif), voire des erreurs cartographiques côté client
        @NotNull(message = "La latitude de départ est obligatoire.")
        @DecimalMin(value = "-90.0", message = "La latitude de départ doit être comprise entre -90 et 90.")
        @DecimalMax(value = "90.0", message = "La latitude de départ doit être comprise entre -90 et 90.")
        Double latDepart, // Coordonnée GPS Latitude du départ

        @NotNull(message = "La longitude de départ est obligatoire.")
        @DecimalMin(value = "-180.0", message = "La longitude de départ doit être comprise entre -180 et 180.")
        @DecimalMax(value = "180.0", message = "La longitude de départ doit être comprise entre -180 et 180.")
        Double lngDepart, // Coordonnée GPS Longitude du départ

        // Point de destination (lat/lng + libellé) — obligatoire
        @NotBlank(message = "L'adresse d'arrivée est obligatoire.")
        @Size(max = 255, message = "L'adresse d'arrivée ne peut pas dépasser 255 caractères.")
        String adresseArrivee, // Nom lisible de la destination

        @NotNull(message = "La latitude d'arrivée est obligatoire.")
        @DecimalMin(value = "-90.0", message = "La latitude d'arrivée doit être comprise entre -90 et 90.")
        @DecimalMax(value = "90.0", message = "La latitude d'arrivée doit être comprise entre -90 et 90.")
        Double latArrivee, // Coordonnée GPS Latitude de la destination

        @NotNull(message = "La longitude d'arrivée est obligatoire.")
        @DecimalMin(value = "-180.0", message = "La longitude d'arrivée doit être comprise entre -180 et 180.")
        @DecimalMax(value = "180.0", message = "La longitude d'arrivée doit être comprise entre -180 et 180.")
        Double lngArrivee // Coordonnée GPS Longitude de la destination
) { } // Enregistrement immuable désérialisé par Jackson puis validé par Bean Validation