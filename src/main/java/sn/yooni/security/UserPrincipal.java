package sn.yooni.security; // Déclaration du package security (authentification & autorisation)

import sn.yooni.model.User; // Importation de l'entité User

import lombok.AllArgsConstructor; // Génération d'un constructeur avec tous les arguments
import lombok.Getter; // Génération automatique des getters

import org.springframework.security.core.GrantedAuthority; // Autorité accordée à l'utilisateur
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Autorité simple (ROLE_xxx)
import org.springframework.security.core.userdetails.UserDetails; // Interface Spring Security représentant un utilisateur

import java.util.Collection; // Collection d'autorités
import java.util.List; // Liste d'autorités

@Getter // Génère les getters automatiquement
@AllArgsConstructor // Génère un constructeur prenant tous les champs
public class UserPrincipal implements UserDetails { // Adapteur entre l'entité User et Spring Security

    private final Long id; // Identifiant de l'utilisateur (utilisé pour générer le JWT)
    private final String email; // Identifiant de connexion (username)
    private final String password; // Hash BCrypt du mot de passe (ne sort jamais en JSON)
    private final boolean actif; // Indique si le compte est actif
    private final Collection<? extends GrantedAuthority> authorities; // Rôles accordés (ROLE_CLIENT, ROLE_ADMIN...)

    // Fabrique transformant une entité User en UserPrincipal pour le moteur de sécurité
    public static UserPrincipal fromUser(User user) {
        // Conversion du rôle en autorité (ex: ROLE_ADMIN)
        List<SimpleGrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        // Construit l'adapteur avec les données nécessaires uniquement
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getActif() != null && user.getActif(),
                grantedAuthorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Autorités exposées au moteur de sécurité
        return authorities;
    }

    @Override
    public String getPassword() { // Hash du mot de passe utilisé pour la vérification d'authentification
        return password;
    }

    @Override
    public String getUsername() { // Identifiant de connexion = adresse e-mail
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { // Le compte n'exprime jamais (durée de vie gérée par l'administrateur)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // Le compte n'est pas verrouillé (verrouillage futur via actif/statut)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // Les identifiants n'ont pas expiré (géré par JWT)
        return true;
    }

    @Override
    public boolean isEnabled() { // Le compte est-il actif ?
        return actif;
    }

}