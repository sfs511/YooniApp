package sn.yooni.dto; // Déclaration du package dto (objets de transfert de données)

import sn.yooni.model.MoyenPaiement; // Importation de l'énumération MoyenPaiement
import sn.yooni.model.StatutPaiement; // Importation de l'énumération StatutPaiement

import java.math.BigDecimal; // Montant au format décimal précis
import java.time.LocalDateTime; // Horodatage des étapes du paiement

// Représentation publique d'un paiement (aucune donnée bancaire sensible)
public record PaiementDTO(
        Long id, // Identifiant technique du paiement
        Long livraisonId, // Identifiant de la livraison concernée
        BigDecimal montant, // Montant réglé en FCFA
        MoyenPaiement moyenPaiement, // Méthode de paiement (WAVE, ORANGE_MONEY...)
        StatutPaiement statutPaiement, // État du paiement (EN_ATTENTE, PAYE...)
        String referenceTransaction, // Référence retournée par le fournisseur
        LocalDateTime createdAt // Date d'initiation du paiement
) { } // Enregistrement immuable sérialisé par Jackson