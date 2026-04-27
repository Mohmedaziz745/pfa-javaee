package com.fsm.shopflw.dto.auth;

import com.fsm.shopflw.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Le mot de passe doit contenir minuscule, majuscule et chiffre")
        String motDePasse,
        @NotBlank String prenom,
        @NotBlank String nom,
        @NotNull Role role,
        String nomBoutique,
        String descriptionBoutique,
        String logoBoutique
) {
}
