package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.InvalidResolutionDateException;
import org.efrei.argosbackend.domain.model.enums.IncidentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record Incident(
        UUID id,
        Equipment equipment,
        String description,
        LocalDateTime openedAt,
        Provider provider,
        LocalDateTime resolvedAt
) {

    public Incident {
        if (resolvedAt != null && resolvedAt.isBefore(openedAt)) {
            throw new InvalidResolutionDateException(id);
        }
    }

    public boolean isResolved() {
        return resolvedAt != null;
    }

    public IncidentStatus status() {
        if (isResolved()) {
            return IncidentStatus.RESOLVED;
        }
        return provider == null ? IncidentStatus.UNASSIGNED : IncidentStatus.ASSIGNED;
    }
}
