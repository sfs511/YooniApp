package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Livreur; // Importation de l'entité Livreur
import sn.yooni.model.StatutLivreur; // Importation de l'énumération StatutLivreur
import sn.yooni.model.User; // Importation de l'entité User

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.List; // Liste de livreurs
import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface LivreurRepository extends JpaRepository<Livreur, Long> { // Dépôt JPA lié à l'entité Livreur

    // Retrouve le profil livreur associé à un compte utilisateur
    Optional<Livreur> findByUser(User user); // Un seul profil par utilisateur (contrainte unique)

    // Liste des livreurs dans un état opérationnel donné
    List<Livreur> findByStatut(StatutLivreur statut); // Ex : tous les livreurs DISPONIBLES

    // Liste des livreurs actuellement disponibles et libres d'accepter des courses
    List<Livreur> findByDisponibleTrueAndStatut(StatutLivreur statut); // Filtre couramment utilisé par l'optimisation des courses

}