package sn.yooni.model; // Déclaration du package model contenant les entités et énumérations du domaine

// Énumération définissant les différents rôles d'accès pour le contrôle de sécurité RBAC
public enum UserRole {

    // Rôle réservé aux utilisateurs clients passant des demandes de transport VTC ou de livraisons
    ROLE_CLIENT,

    // Rôle réservé aux chauffeurs et livreurs acceptant et réalisant les courses
    ROLE_LIVREUR,

    // Rôle d'administration globale donnant accès au tableau de bord, à la gestion des comptes et aux audits
    ROLE_ADMIN

}