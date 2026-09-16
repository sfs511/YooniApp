package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Client; // Importation de l'entité Client
import sn.yooni.model.User; // Importation de l'entité User

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface ClientRepository extends JpaRepository<Client, Long> { // Dépôt JPA lié à l'entité Client

    // Retrouve le profil client associé à un compte utilisateur (relation 1:1 — contrainte unique)
    Optional<Client> findByUser(User user); // Un seul profil client par compte

}