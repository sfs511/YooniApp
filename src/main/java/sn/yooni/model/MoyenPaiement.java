package sn.yooni.model; // Déclaration du package model contenant les structures de données du domaine

// Énumération représentant les moyens de paiement acceptés sur la plateforme Yooni
public enum MoyenPaiement {

    // Paiement mobile via le service WAVE (très répandu au Sénégal)
    WAVE,

    // Paiement mobile via ORANGE MONEY (opérateur Orange Sénégal)
    ORANGE_MONEY,

    // Paiement mobile via FREE MONEY (opérateur Free Sénégal)
    FREE_MONEY,

    // Paiement en espèces remis directement au livreur
    ESPECES

}