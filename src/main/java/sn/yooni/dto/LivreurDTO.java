package sn.yooni.dto; // Déclaration du package dto (objets de transfert de données)

import sn.yooni.model.StatutLivreur; // Importation de l'énumération StatutLivreur

import java.math.BigDecimal; // Note moyenne au format décimal précis

// Représentation publique d'un profil livreur/chauffeur (aucune donnée sensible).
// NB : le numéro de permis est volontairement absent de ce DTO public — c'est une
// donnée personnelle (PII) qui ne doit être visible que par l'administration.
public record LivreurDTO(
        Long id, // Identifiant technique du profil livreur
        String nom, // Nom de famille du livreur
        String prenom, // Prénom du livreur
        String email, // Adresse e-mail du compte associé
        String telephone, // Numéro de téléphone du compte associé
        String photoUrl, // URL de la photo de profil (facultatif)
        StatutLivreur statut, // État opérationnel (DISPONIBLE, EN_COURSE...)
        BigDecimal noteMoyenne, // Note moyenne sur 5
        Boolean disponible // Indique si le livreur accepte les courses
) { } // Enregistrement immuable sérialisé par Jackson