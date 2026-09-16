package sn.yooni.dto.request; // Déclaration du package dto.request (requêtes HTTP entrantes)

import sn.yooni.model.UserRole; // Rôles applicatifs (CLIENT / LIVREUR)

import jakarta.validation.constraints.Email; // Validation du format e-mail
import jakarta.validation.constraints.NotBlank; // Validation de non-vide
import jakarta.validation.constraints.NotNull; // Validation de présence obligatoire
import jakarta.validation.constraints.Pattern; // Validation par expression régulière
import jakarta.validation.constraints.Size; // Validation de longueur

// Requête de création de compte (point public de l'API) — client ou livreur
public record RegisterRequest(

        @NotBlank(message = "L'e-mail est obligatoire.") // Champ obligatoire
        @Email(message = "Le format de l'e-mail est invalide.") // Format e-mail valide exigé
        String email, // Adresse e-mail de connexion

        @NotBlank(message = "Le mot de passe est obligatoire.") // Champ obligatoire
        @Size(min = 8, max = 72, message = "Le mot de passe doit contenir entre 8 et 72 caractères.") // Limite BCrypt = 72 octets
        String password, // Mot de passe en clair (haché avec BCrypt avant l'enregistrement)

        @NotBlank(message = "Le numéro de téléphone est obligatoire.") // Champ obligatoire
        @Pattern(regexp = "\\+?[0-9]{9,15}", message = "Le numéro de téléphone est invalide (ex: +221771234567).") // Format Sénégal/International
        String telephone, // Numéro de téléphone (unique en base)

        @NotBlank(message = "Le nom est obligatoire.") // Champ obligatoire
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.") // Longueur maximale
        String nom, // Nom de famille

        @NotBlank(message = "Le prénom est obligatoire.") // Champ obligatoire
        @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.") // Longueur maximale
        String prenom, // Prénom

        @NotNull(message = "Le rôle est obligatoire.") // Champ obligatoire (CLIENT ou LIVREUR)
        UserRole role, // Rôle du compte à créer (les comptes ADMIN sont créés uniquement en base)

        @Size(max = 50, message = "Le numéro de permis ne peut pas dépasser 50 caractères.") // Longueur maximale
        String permisNumero // Numéro du permis — OBLIGATOIRE et unique pour un compte LIVREUR, ignoré pour un client
) { } // Enregistrement immuable, sérialisé automatiquement par Jackson