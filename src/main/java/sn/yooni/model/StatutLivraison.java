package sn.yooni.model; // Déclaration du package model regroupant les structures métier

// Énumération représentant le statut d'avancement d'une livraison ou d'une course VTC
public enum StatutLivraison {

    // La demande de course ou livraison est créée par le client et attend la confirmation d'un chauffeur
    EN_ATTENTE,

    // Un chauffeur ou livreur a accepté la demande et se dirige vers le point de départ
    ACCEPTEE,

    // Le chauffeur a récupéré le client ou le colis et le trajet vers la destination est en cours
    EN_COURS,

    // Le trajet s'est achevé avec succès, le client a été déposé ou le colis a été livré
    TERMINEE,

    // La demande a été annulée soit par le client soit par le livreur avant son achèvement
    ANNULEE

}