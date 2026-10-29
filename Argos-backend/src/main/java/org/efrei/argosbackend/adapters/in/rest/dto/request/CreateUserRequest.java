package org.efrei.argosbackend.adapters.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.efrei.argosbackend.domain.model.enums.ProfileType;

public record CreateUserRequest(
        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String lastName,

        @NotBlank(message = "L'identifiant est obligatoire.")
        @Size(max = 50, message = "L'identifiant ne doit pas dépasser 50 caractères.")
        String username,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, max = 72, message = "Le mot de passe doit contenir entre 8 et 72 caractères.")
        String password,

        @NotNull(message = "Le profil est obligatoire.")
        ProfileType profile
) {
}
