package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.User; // Importation de l'entité User

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface UserRepository extends JpaRepository<User, Long> { // Dépôt JPA lié à l'entité User

    // Recherche d'un utilisateur par son adresse e-mail (colonne unique en base)
    Optional<User> findByEmail(String email); // Retourne un Optional<User> jamais nul

    // Vérifie si une adresse e-mail est déjà utilisée (contrainte d'unicité)
    boolean existsByEmail(String email); // Retourne true si l'e-mail existe déjà

    // Vérifie si un numéro de téléphone est déjà utilisé (contrainte d'unicité)
    boolean existsByTelephone(String telephone); // Retourne true si le téléphone existe déjà

}