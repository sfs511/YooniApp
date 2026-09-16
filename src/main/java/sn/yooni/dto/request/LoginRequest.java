package sn.yooni.dto.request; // Déclaration du package dto.request (requêtes HTTP entrantes)

import jakarta.validation.constraints.Email; // Validation du format e-mail
import jakarta.validation.constraints.NotBlank; // Validation de non-vide
import jakarta.validation.constraints.Size; // Validation de longueur

// Requête d'authentification par e-mail et mot de passe
public record LoginRequest(

        @NotBlank(message = "L'e-mail est obligatoire.") // Champ obligatoire
        @Email(message = "Le format de l'e-mail est invalide.") // Format e-mail valide exigé
        String email, // Adresse e-mail de connexion

        @NotBlank(message = "Le mot de passe est obligatoire.") // Champ obligatoire
        @Size(max = 72, message = "Le mot de passe ne peut pas dépasser 72 caractères.") // Limite BCrypt, identique à l'inscription
        String password // Mot de passe en clair vérifié par l'AuthenticationManager
) { } // Enregistrement immuable, sérialisé automatiquement par Jackson