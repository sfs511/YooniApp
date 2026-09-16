package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Livreur; // Importation de l'entité Livreur
import sn.yooni.model.Vehicule; // Importation de l'entité Vehicule

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> { // Dépôt JPA lié à l'entité Vehicule

    // Retrouve le véhicule d'un livreur (un seul véhicule par livreur — contrainte unique)
    Optional<Vehicule> findByLivreur(Livreur livreur); // Consultation du véhicule d'un chauffeur

}