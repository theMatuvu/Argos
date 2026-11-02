package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.IncidentAlreadyResolvedException;
import org.efrei.argosbackend.domain.exception.InvalidResolutionDateException;
import org.efrei.argosbackend.domain.model.enums.IncidentStatus;

import java.time.Duration;
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

    public static final int DESCRIPTION_MIN_LENGTH = 10;
    public static final int DESCRIPTION_MAX_LENGTH = 500;

    public Incident {
        if (resolvedAt != null && resolvedAt.isBefore(openedAt)) {
            throw new InvalidResolutionDateException(id);
        }
    }

    public static Incident declare(Equipment equipment, String description, LocalDateTime openedAt) {
        return new Incident(null, equipment, description, openedAt, null, null);
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

    public Duration openDuration(LocalDateTime now) {
        return Duration.between(openedAt, isResolved() ? resolvedAt : now);
    }

    public Incident withId(UUID persistedId) {
        return new Incident(persistedId, equipment, description, openedAt, provider, resolvedAt);
    }

    public Incident withDescription(String newDescription) {
        ensureNotResolved();
        return new Incident(id, equipment, newDescription, openedAt, provider, resolvedAt);
    }

    public Incident assignTo(Provider newProvider) {
        ensureNotResolved();
        return new Incident(id, equipment, description, openedAt, newProvider, resolvedAt);
    }

    public Incident resolve(LocalDateTime resolutionTime) {
        ensureNotResolved();
        return new Incident(id, equipment, description, openedAt, provider, resolutionTime);
    }

    private void ensureNotResolved() {
        if (isResolved()) {
            throw new IncidentAlreadyResolvedException(id);
        }
    }
}
