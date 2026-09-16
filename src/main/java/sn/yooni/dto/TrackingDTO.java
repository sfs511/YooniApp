package sn.yooni.dto; // Déclaration du package dto (objets de transfert de données)

import java.time.LocalDateTime; // Horodatage de la position GPS

// Représentation publique d'une position GPS (suivi temps réel des livreurs)
public record TrackingDTO(
        Long livreurId, // Identifiant du livreur suivi
        double latitude, // Latitude GPS
        double longitude, // Longitude GPS
        LocalDateTime horodatage, // Instant de capture de la position
        Double vitesseKmh // Vitesse éventuelle calculée (facultatif)
) { } // Enregistrement immuable sérialisé par Jackson