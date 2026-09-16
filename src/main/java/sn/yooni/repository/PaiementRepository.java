package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Livraison; // Importation de l'entité Livraison
import sn.yooni.model.Paiement; // Importation de l'entité Paiement
import sn.yooni.model.StatutPaiement; // Importation de l'énumération StatutPaiement

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.List; // Liste de paiements
import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface PaiementRepository extends JpaRepository<Paiement, Long> { // Dépôt JPA lié à l'entité Paiement

    // Paiement d'une livraison (relation 1:1 — contrainte unique)
    Optional<Paiement> findByLivraison(Livraison livraison); // Consultation du paiement d'une course

    // Paiements selon leur statut financier
    List<Paiement> findByStatutPaiement(StatutPaiement statutPaiement); // Ex : tous les paiements EN_ATTENTE

}