package sn.yooni.model; // Déclaration du package model contenant les structures de données du domaine

// Énumération représentant l'état opérationnel et la disponibilité d'un chauffeur ou livreur
public enum StatutLivreur {

    // Le livreur est connecté, en ligne et prêt à recevoir de nouvelles demandes de course
    DISPONIBLE,

    // Le livreur a accepté une commande et effectue actuellement une course ou un trajet
    EN_COURSE,

    // Le livreur est déconnecté de la plateforme ou en pause et ne reçoit pas de demandes
    INACTIF,

    // Le compte du livreur est suspendu par un administrateur (ex: contrôle de permis ou litige)
    SUSPENDU

}