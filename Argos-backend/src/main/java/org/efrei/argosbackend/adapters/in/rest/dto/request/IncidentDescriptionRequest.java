package org.efrei.argosbackend.adapters.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.efrei.argosbackend.domain.model.Incident;

public record IncidentDescriptionRequest(
        @NotBlank(message = "La description est obligatoire.")
        @Size(
                min = Incident.DESCRIPTION_MIN_LENGTH,
                max = Incident.DESCRIPTION_MAX_LENGTH,
                message = "La description doit contenir entre {min} et {max} caractères."
        )
        String description
) {
}
