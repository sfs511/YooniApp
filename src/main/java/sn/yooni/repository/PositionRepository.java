package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Livreur; // Importation de l'entité Livreur
import sn.yooni.model.Position; // Importation de l'entité Position

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.time.LocalDateTime; // Borne temporelle pour l'historique des positions
import java.util.List; // Liste de positions

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface PositionRepository extends JpaRepository<Position, Long> { // Dépôt JPA lié à l'entité Position

    // Position la plus récente d'un livreur (tracé GPS temps réel)
    Position findFirstByLivreurOrderByHorodatageDesc(Livreur livreur); // Dernière position connue

    // Historique des positions d'un livreur depuis une date donnée
    List<Position> findByLivreurAndHorodatageAfterOrderByHorodatageAsc(Livreur livreur, LocalDateTime depuis);
    // Cronologie triée pour rejouer le trajet d'une course

}