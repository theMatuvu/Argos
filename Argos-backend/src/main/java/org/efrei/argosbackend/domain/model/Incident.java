package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.enums.IncidentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record Incident(
        UUID id,
        Equipment equipment,
        String description,
        LocalDateTime createdAt,
        IncidentStatus incidentStatus,
        Provider provider
) {
}
