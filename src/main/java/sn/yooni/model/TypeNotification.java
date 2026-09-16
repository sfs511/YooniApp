package sn.yooni.model; // Déclaration du package model contenant les structures de données du domaine

// Énumération définissant les différents types de notifications gérées par l'application
public enum TypeNotification {

    // Notification émise lorsqu'un client enregistre une nouvelle demande de course
    COURSE_DEMANDEE,

    // Notification émise à destination du client lorsqu'un chauffeur accepte la prise en charge
    COURSE_ACCEPTEE,

    // Notification émise en cas d'annulation d'une course par le client ou le chauffeur
    COURSE_ANNULEE,

    // Notification informant le client que le chauffeur est arrivé au point de rendez-vous
    LIVREUR_ARRIVE,

    // Notification confirmant la fin de la course ou la remise effective du colis
    LIVRAISON_TERMINEE,

    // Notification de confirmation de paiement (Wave, Orange Money, Espèces)
    PAIEMENT_VALIDE,

    // Alertes administratives et messages d'information système
    SYSTEME

}