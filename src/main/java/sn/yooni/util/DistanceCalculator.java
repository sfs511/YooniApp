package sn.yooni.util; // Déclaration du package utilitaire (helpers sans état)

import lombok.experimental.UtilityClass; // Indique que la classe est un utilitaire pur (constructeur privée généré)

@UtilityClass // Empêche l'instanciation de la classe (toutes les méthodes sont statiques)
public class DistanceCalculator { // Calcul de distance géographique (Haversine)

    // Rayon moyen de la Terre en kilomètres
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Calcule la distance entre deux points GPS en kilomètres (formule de Haversine)
    // Précision suffisante pour les distances intra-urbaines (< 1 km d'erreur)
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Conversion des degrés en radians (required par les fonctions trigonométriques Java)
        double dLat = Math.toRadians(lat2 - lat1); // Écart de latitude en radians
        double dLon = Math.toRadians(lon2 - lon1); // Écart de longitude en radians
        double lat1Rad = Math.toRadians(lat1); // Latitude du premier point en radians
        double lat2Rad = Math.toRadians(lat2); // Latitude du deuxième point en radians

        // Formule de Haversine : a = sin²(dlat/2) + cos(lat1) * cos(lat2) * sin²(dlon/2)
        double a = Math.pow(Math.sin(dLat / 2), 2)
                 + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);

        // c = 2 * atan2(√a, √(1-a)) : distance angulaire
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // d = R * c : distance en kilomètres
        return EARTH_RADIUS_KM * c;
    }

    // Estime le temps de parcours en minutes à partir de la distance et d'une vitesse moyenne
    public double estimateDuration(double distanceKm, double averageSpeedKmh) {
        if (averageSpeedKmh <= 0) {
            averageSpeedKmh = 30.0; // Vitesse par défaut pour Dakar intra-urbain (30 km/h)
        }
        return (distanceKm / averageSpeedKmh) * 60.0; // Durée en minutes
    }

    // Calcule le tarif estimé en FCFA selon la distance (tarification simplifiée)
    public double estimateFare(double distanceKm) {
        double baseFare = 500.0;   // Prix de départ (prise en charge du client ou du colis)
        double perKm = 200.0;      // Prix par kilomètre parcouru
        return baseFare + (distanceKm * perKm); // Tarif total en FCFA
    }

}