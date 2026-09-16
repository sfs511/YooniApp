package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import sn.yooni.model.User; // Importation de l'entité User
import sn.yooni.repository.UserRepository; // Importation du dépôt User

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.security.core.userdetails.UserDetails; // Interface représentant un utilisateur authentifié
import org.springframework.security.core.userdetails.UserDetailsService; // Contrat de chargement des utilisateurs
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Erreur utilisateur introuvable
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion de la transaction de lecture

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection de UserRepository
public class CustomUserDetailsService implements UserDetailsService { // Service de chargement des comptes pour l'Auth

    private final UserRepository userRepository; // Dépôt d'accès aux comptes utilisateurs

    @Override
    @Transactional(readOnly = true) // Lecture seule, aucune écriture en base
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche le compte par e-mail ; s'il n'existe pas, l'authentification échoue proprement
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun compte trouvé avec cet e-mail : " + email));
        // Conversion de l'entité en adapteur utilisable par Spring Security
        return UserPrincipal.fromUser(user);
    }

}