package sn.yooni.model; // Déclaration du package model contenant les structures de données du domaine

// Énumération représentant l'état d'avancement d'une transaction financière
public enum StatutPaiement {

    // Le paiement a été enregistré mais n'est pas encore confirmé par le fournisseur de paiement
    EN_ATTENTE,

    // Le paiement a été validé et encaissé avec succès
    PAYE,

    // Le paiement a échoué (solde insuffisant, fournisseur indisponible, etc.)
    ECHOUE,

    // Le montant a été restitué au client (annulation de course ou litige)
    REMBOURSE

}