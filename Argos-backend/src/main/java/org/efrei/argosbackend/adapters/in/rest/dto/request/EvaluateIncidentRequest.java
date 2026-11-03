package org.efrei.argosbackend.adapters.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import org.efrei.argosbackend.domain.model.Rating;
import org.hibernate.validator.constraints.Range;

public record EvaluateIncidentRequest(
        @NotNull(message = "La note est obligatoire.")
        @Range(
                min = Rating.MIN_VALUE,
                max = Rating.MAX_VALUE,
                message = "La note doit être un entier compris entre {min} et {max}."
        )
        Integer rating
) {
}
