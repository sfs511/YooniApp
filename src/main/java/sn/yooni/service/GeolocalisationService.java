package sn.yooni.service; // Déclaration du package service (logique métier)

import org.springframework.stereotype.Service; // Stéréotype service métier

@Service // Bean service enregistré dans le conteneur Spring
public class GeolocalisationService { // Services de géolocalisation (coordonnées et distances)

    // TODO Nouvelle étape : implémenter les helpers géographiques :
    // - géocodage adresse → coordonnées (API externe : Nominatim, Google Maps)
    // - calcul de distance et durée de trajet entre deux points (utilise DistanceCalculator)
    // - vérification de la proximité d'un livreur avec le point de prise en charge
}