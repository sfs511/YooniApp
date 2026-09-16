package sn.yooni.dto; // Déclaration du package dto (objets de transfert de données)

// Représentation publique d'un profil client (aucune donnée sensible)
public record ClientDTO(
        Long id, // Identifiant technique du profil client
        String nom, // Nom de famille du client
        String prenom, // Prénom du client
        String email, // Adresse e-mail du compte associé
        String telephone, // Numéro de téléphone du compte associé
        String photoUrl // URL de la photo de profil (facultatif)
) { } // Enregistrement immuable sérialisé par Jackson