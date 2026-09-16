package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Colis; // Importation de l'entité Colis
import sn.yooni.model.Livraison; // Importation de l'entité Livraison

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface ColisRepository extends JpaRepository<Colis, Long> { // Dépôt JPA lié à l'entité Colis

    // Retrouve le colis associé à une livraison donnée (relation 1:1 — contrainte unique)
    Optional<Colis> findByLivraison(Livraison livraison); // Consultation du colis d'une livraison

}