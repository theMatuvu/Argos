package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.IncidentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String description,
        LocalDateTime openedAt,
        IncidentStatus status,
        ProviderResponse provider,
        LocalDateTime resolvedAt,
        Integer rating
) {
}
