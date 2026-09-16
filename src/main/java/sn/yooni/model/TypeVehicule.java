package sn.yooni.model; // Déclaration du package model contenant les structures de données du domaine

// Énumération représentant les catégories de véhicules autorisées sur la plateforme
public enum TypeVehicule {

    // Moto / mobylette (souvent utilisées pour les livraisons rapides de colis)
    MOTO,

    // Voiture berline / citadine (course VTC classique)
    BERLINE,

    // SUV / 4x4 (confort supérieur, trajets urbains ou interurbains)
    SUV,

    // Camionnette légère (livraison de gros volumes ou colis volumineux)
    CAMIONETTE
}