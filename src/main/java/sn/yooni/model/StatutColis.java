package sn.yooni.model; // Déclaration du package model regroupant les entités et énumérations du domaine

// Énumération représentant les étapes du cycle de vie spécifique d'un colis transporté
public enum StatutColis {

    // Le colis est en attente de récupération auprès de l'expéditeur par le livreur
    EN_ATTENTE_RETRAIT,

    // Le colis a été récupéré par le livreur et est en cours d'acheminement vers le destinataire
    EN_TRANSIT,

    // Le colis a été remis au destinataire en échange de la validation du code secret
    LIVRE,

    // La livraison du colis a échoué (destinataire absent, adresse erronée ou refus du colis)
    ECHOUE

}