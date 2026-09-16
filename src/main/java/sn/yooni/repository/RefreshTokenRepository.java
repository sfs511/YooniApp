package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.RefreshToken; // Importation de l'entité RefreshToken
import sn.yooni.model.User; // Importation de l'entité User

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.Optional; // Gestion propre des retours éventuellement vides

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> { // Dépôt JPA lié à l'entité RefreshToken

    // Recherche d'un jeton de rafraîchissement par son empreinte SHA-256 (colonne unique en base).
    // NB : la base ne stocke JAMAIS le jeton en clair — uniquement son hash (voir AuthService.hashToken).
    Optional<RefreshToken> findByToken(String tokenHash); // Retourne un Optional<RefreshToken> jamais nul

    // Retrouve le jeton de rafraîchissement actif d'un utilisateur donné
    Optional<RefreshToken> findByUser(User user); // La contrainte unique user_id garantit au plus un résultat

    // Supprime tous les jetons d'un utilisateur (utilisé lors de la déconnexion complète)
    void deleteByUser(User user); // Purge des refresh tokens de l'utilisateur concerné

}