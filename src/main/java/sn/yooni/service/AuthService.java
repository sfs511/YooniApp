package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.config.JwtProperties; // Durées de validité des jetons
import sn.yooni.dto.request.LoginRequest; // Requête de connexion
import sn.yooni.dto.request.RegisterRequest; // Requête de création de compte
import sn.yooni.dto.response.JwtResponse; // Réponse standard de l'authentification
import sn.yooni.dto.response.UserResponse; // Profil sécurisé de l'utilisateur
import sn.yooni.exception.BusinessException; // Erreur métier portant un code HTTP
import sn.yooni.model.Client; // Entité profil client
import sn.yooni.model.Livreur; // Entité profil livreur
import sn.yooni.model.RefreshToken; // Entité jeton de rafraîchissement
import sn.yooni.model.StatutLivreur; // État opérationnel du livreur
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.model.UserRole; // Rôles applicatifs
import sn.yooni.repository.ClientRepository; // Dépôt des profils clients
import sn.yooni.repository.LivreurRepository; // Dépôt des profils livreurs
import sn.yooni.repository.RefreshTokenRepository; // Dépôt des jetons de rafraîchissement
import sn.yooni.repository.UserRepository; // Dépôt des comptes utilisateurs
import sn.yooni.security.JwtTokenProvider; // Fournisseur de génération et validation des jetons

import java.math.BigDecimal; // Note moyenne initiale du livreur (5.0)

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.http.HttpStatus; // Codes de statut HTTP
import org.springframework.security.authentication.AuthenticationManager; // Vérification e-mail + mot de passe
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Identifiants fournis au gestionnaire
import org.springframework.security.crypto.password.PasswordEncoder; // Hachage BCrypt
import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

import java.time.Instant; // Instant courant pour vérifier l'expiration des jetons

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection des dépendances
public class AuthService { // Service central du cycle de vie des sessions (inscription, connexion, rafraîchissement)

    private final UserRepository userRepository; // Accès aux comptes utilisateurs
    private final ClientRepository clientRepository; // Accès aux profils clients
    private final LivreurRepository livreurRepository; // Accès aux profils livreurs
    private final RefreshTokenRepository refreshTokenRepository; // Accès aux jetons de rafraîchissement
    private final PasswordEncoder passwordEncoder; // Hachage BCrypt des mots de passe
    private final AuthenticationManager authenticationManager; // Vérification des identifiants
    private final JwtTokenProvider jwtTokenProvider; // Fournisseur de génération et validation des jetons JWT
    private final JwtProperties jwtProperties; // Durées de validité des jetons
    private final AuditLogService auditLogService; // Traçabilité des créations de comptes

    // Crée un compte (client ou livreur) et retourne une session ouverte
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        // L'inscription publique ne permet jamais de créer un compte administrateur
        if (request.role() == UserRole.ROLE_ADMIN) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Création d'un compte administrateur interdite.");
        }
        // Vérifie l'unicité de l'e-mail (contrainte base : uk_users_email)
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Un compte existe déjà avec cet e-mail.");
        }
        // Vérifie l'unicité du téléphone (contrainte base : uk_users_telephone)
        if (userRepository.existsByTelephone(request.telephone())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Un compte existe déjà avec ce numéro de téléphone.");
        }

        // Construit le compte utilisateur avec le mot de passe HACHÉ (BCrypt) — jamais stocké en clair
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password())); // Hachage BCrypt (sel intégré)
        user.setTelephone(request.telephone());
        user.setRole(request.role()); // Rôle demandé : CLIENT ou LIVREUR
        user.setActif(true); // Compte actif dès l'enregistrement
        User savedUser = userRepository.save(user); // Persiste le compte en base

        // Crée le profil associé au rôle demandé (1:1 avec le compte)
        if (request.role() == UserRole.ROLE_LIVREUR) {
            createLivreurProfile(savedUser, request); // Profil livreur (permis obligatoire)
        } else {
            createClientProfile(savedUser, request); // Profil client
        }

        // Trace la création dans le journal d'audit (traçabilité des comptes)
        auditLogService.record("REGISTER", "User", savedUser.getId(),
                "Création de compte " + request.role() + ".", savedUser);

        // Ouvre la session directement : émet access + refresh token et retourne la réponse
        return buildAuthResponse(savedUser);
    }

    // Construit et persiste le profil client 1:1 (nom, prénom obligatoires)
    private void createClientProfile(User savedUser, RegisterRequest request) {
        Client client = new Client();
        client.setUser(savedUser);
        client.setNom(request.nom());
        client.setPrenom(request.prenom());
        clientRepository.save(client); // Persiste le profil client
    }

    // Construit et persiste le profil livreur 1:1 (permis de conduire obligatoire et unique)
    private void createLivreurProfile(User savedUser, RegisterRequest request) {
        // Le numéro de permis est indispensable pour un compte livreur
        if (request.permisNumero() == null || request.permisNumero().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Le numéro de permis est obligatoire pour un compte livreur.");
        }
        Livreur livreur = new Livreur();
        livreur.setUser(savedUser);
        livreur.setNom(request.nom());
        livreur.setPrenom(request.prenom());
        livreur.setPermisNumero(request.permisNumero());
        livreur.setStatut(StatutLivreur.INACTIF); // Hors ligne tant qu'il n'active pas sa disponibilité
        livreur.setDisponible(false); // Indisponible par défaut
        livreur.setNoteMoyenne(BigDecimal.valueOf(5.0)); // Note initiale neutre (5.0)
        livreurRepository.save(livreur); // Persiste le profil livreur
    }

    // Authentifie un client par e-mail/mot de passe et retourne une session ouverte
    @Transactional
    public JwtResponse login(LoginRequest request) {
        // Délègue la vérification à l'AuthenticationManager (DaoAuthenticationProvider + BCrypt).
        // En cas d'échec, BadCredentialsException est levée et mappée en 401 par le GlobalExceptionHandler.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        // L'authentification a réussi : charge le compte pour générer les jetons
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "E-mail ou mot de passe incorrect."));

        // Trace l'événement de connexion dans le journal d'audit (détection d'accès anormaux)
        auditLogService.record("LOGIN", "User", user.getId(), "Connexion réussie.", user);

        // Construit et retourne une session complète (access + refresh tokens)
        return buildAuthResponse(user);
    }

    // Renouvelle la session à partir d'un Refresh Token encore valide (rotation de jeton)
    @Transactional
    public JwtResponse refresh(String rawRefreshToken) {
        // La base ne stocke JAMAIS le jeton en clair (voir hashToken) : on recherche
        // par l'empreinte SHA-256 pour comparer sans conserver la valeur réelle.
        RefreshToken stored = refreshTokenRepository.findByToken(hashToken(rawRefreshToken))
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh token invalide ou inconnu."));

        // Rejette tout jeton révoqué (déconnexion effectuée)
        if (Boolean.TRUE.equals(stored.getRevoked())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh token révoqué. Veuillez vous reconnecter.");
        }
        // Rejette tout jeton expiré (au-delà de 7 jours)
        if (stored.getExpiryDate().isBefore(Instant.now())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh token expiré. Veuillez vous reconnecter.");
        }

        User user = stored.getUser(); // Utilisateur propriétaire du jeton
        // Refuse le renouvellement si le compte n'est plus actif
        if (user.getActif() == null || !user.getActif()) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Votre compte est désactivé.");
        }

        // Rotation du refresh token : nouvelle valeur + nouvelle expiration (repossession).
        // Cette rotation rend l'ancien jeton inutilisable (son hash ne correspond plus),
        // ce qui limite la fenêtre d'exploitation en cas de vol.
        String nouveauJeton = jwtTokenProvider.generateRefreshToken(user); // Nouveau jeton en clair
        stored.setToken(hashToken(nouveauJeton)); // Seule l'empreinte est persistée
        stored.setRevoked(false); // Réactif après rotation
        stored.setExpiryDate(jwtTokenProvider.computeRefreshExpiration().toInstant()); // Nouvelle expiration
        refreshTokenRepository.save(stored); // Persiste la rotation

        // Construit la réponse avec le nouveau couple de jetons
        return buildTokensFor(user, nouveauJeton);
    }

    // Révoque un Refresh Token (déconnexion explicite) : le jeton devient inutilisable
    @Transactional
    public void logout(String rawRefreshToken) {
        // Recherche par empreinte : si le jeton n'existe pas, la déconnexion est
        // considérée comme réussie (idempotent)
        refreshTokenRepository.findByToken(hashToken(rawRefreshToken)).ifPresent(stored -> {
            stored.setRevoked(true); // Marquage comme révoqué
            refreshTokenRepository.save(stored); // Persiste la révocation
        });
    }

    // Construit une session complète (access + refresh token) pour un utilisateur authentifié.
    // Seule l'empreinte SHA-256 du refresh token est persistée : en cas de fuite de la
    // base de données, les jetons réels restent inviolables côté serveur.
    private JwtResponse buildAuthResponse(User user) {
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken(user); // Jeton en clair (retourné au client)
        // Upsert : un seul jeton actif par utilisateur (contrainte unique user_id)
        RefreshToken stored = refreshTokenRepository.findByUser(user)
                .orElseGet(RefreshToken::new); // Réutilise le jeton existant ou en crée un nouveau
        stored.setUser(user); // Lie le jeton à l'utilisateur
        stored.setToken(hashToken(rawRefreshToken)); // Hash SHA-256 stocké en base (jamais le jeton brut)
        stored.setRevoked(false); // Actif
        stored.setExpiryDate(jwtTokenProvider.computeRefreshExpiration().toInstant()); // Expiration à 7 jours
        storageTimeIfNew(stored); // Alimente la date de création si nouveau
        refreshTokenRepository.save(stored); // Persiste l'upsert

        // Construit et retourne la réponse complète de l'authentification
        return buildTokensFor(user, rawRefreshToken);
    }

    // Assemble la réponse JwtResponse (access token + refresh token + profil).
    // Le refresh token retourné est la valeur EN CLAIR (jamais son hash),
    // car il est destiné au client pour sa prochaine demande de renouvellement.
    private JwtResponse buildTokensFor(User user, String rawRefreshToken) {
        String accessToken = jwtTokenProvider.generateAccessToken(user); // Nouveau token d'accès (24h)
        UserResponse userResponse = toResponse(user); // Profil sécurisé
        return JwtResponse.of(accessToken, rawRefreshToken, jwtProperties.jwtExpirationMs(), userResponse);
    }

    // Hache un refresh token (SHA-256 → hexadécimal) AVANT tout stockage en base.
    // La base ne contient donc jamais la valeur réelle : une fuite de la table
    // refresh_tokens ne permet pas de rejouer les sessions volées.
    private String hashToken(String rawToken) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash); // 64 caractères hexadécimaux
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible : impossible de sécuriser les jetons.", e);
        }
    }

    // Date de création : alimente uniquement pour un jeton nouvellement créé (évite de modifier l'historique)
    private void storageTimeIfNew(RefreshToken stored) {
        if (stored.getCreatedAt() == null) {
            stored.setCreatedAt(Instant.now()); // Horodatage de génération d'un jeton neuf
        }
    }

    // Convertit une entité User en DTO sécurisé (aucun hash ni donnée sensible exposé)
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getTelephone(),
                user.getRole(),
                user.getActif(),
                user.getCreatedAt()
        );
    }

}