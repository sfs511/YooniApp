package sn.yooni.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import sn.yooni.model.UserRole;

public record RegisterRequest(
        @NotBlank @Email(message = "Adresse e-mail invalide") String email,
        @NotBlank @Size(min = 8, max = 100, message = "Le mot de passe doit contenir au moins 8 caracteres") String password,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Numero de telephone invalide") String telephone,
        @NotBlank @Size(max = 100) String nom,
        @NotBlank @Size(max = 100) String prenom,
        @NotNull(message = "Le role est obligatoire (ROLE_CLIENT ou ROLE_LIVREUR)") UserRole role,
        @Size(max = 50) String permisNumero) {
}