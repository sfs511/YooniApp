package sn.yooni.dto.request; // Déclaration du package dto.request (données envoyées par le client)

import sn.yooni.model.MoyenPaiement; // Moyens de paiement acceptés (WAVE, ORANGE_MONEY, FREE_MONEY, ESPECES)

import jakarta.validation.constraints.NotNull; // Validation de présence obligatoire

// Requête d'initiation d'un paiement de course (envoyée par le client après une livraison terminée)
public record InitierPaiementRequest(

        @NotNull(message = "L'identifiant de la livraison est obligatoire.")
        Long livraisonId, // Livraison / course concernée par le paiement

        @NotNull(message = "Le moyen de paiement est obligatoire.")
        MoyenPaiement moyenPaiement // Méthode choisie (Wave, Orange Money, Free Money ou Espèces)
) { } // Enregistrement immuable désérialisé par Jackson puis validé par Bean Validation