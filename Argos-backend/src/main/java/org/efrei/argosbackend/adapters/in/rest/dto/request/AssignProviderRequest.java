package org.efrei.argosbackend.adapters.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignProviderRequest(
        @NotNull(message = "Le prestataire est obligatoire.")
        UUID providerId
) {
}
