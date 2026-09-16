package sn.yooni.dto; // Déclaration du package dto (objets de transfert de données)

import sn.yooni.model.StatutLivraison; // Importation de l'énumération StatutLivraison

import java.math.BigDecimal; // Prix au format décimal précis
import java.time.LocalDateTime; // Horodatage des étapes de la livraison

// Représentation publique d'une livraison / course VTC (aucune donnée sensible)
public record LivraisonDTO(
        Long id, // Identifiant technique de la livraison
        Long clientId, // Identifiant du client demandeur
        Long livreurId, // Identifiant du livreur assigné (null si en attente)
        String adresseDepart, // Point de prise en charge
        double latDepart, // Latitude de départ
        double lngDepart, // Longitude de départ
        String adresseArrivee, // Point de destination
        double latArrivee, // Latitude de destination
        double lngArrivee, // Longitude de destination
        BigDecimal prix, // Tarif total en FCFA
        StatutLivraison statut, // État d'avancement de la course
        Double distanceKm, // Distance estimée du trajet
        Integer dureeEstimeeMin, // Durée estimée du trajet en minutes
        LocalDateTime createdAt // Date de demande de la course
) { } // Enregistrement immuable sérialisé par Jackson