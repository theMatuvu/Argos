package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.IncidentAlreadyEvaluatedException;
import org.efrei.argosbackend.domain.exception.IncidentAlreadyResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentNotResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentWithoutProviderException;
import org.efrei.argosbackend.domain.exception.InvalidResolutionDateException;
import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.efrei.argosbackend.domain.model.enums.IncidentStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record Incident(
        UUID id,
        Equipment equipment,
        String description,
        LocalDateTime openedAt,
        Provider provider,
        LocalDateTime resolvedAt,
        Rating rating
) {

    public static final int DESCRIPTION_MIN_LENGTH = 10;
    public static final int DESCRIPTION_MAX_LENGTH = 500;
    public static final Duration ORANGE_ATTENTION_THRESHOLD = Duration.ofHours(48);
    public static final Duration RED_ATTENTION_THRESHOLD = Duration.ofHours(72);

    public Incident {
        if (resolvedAt != null && resolvedAt.isBefore(openedAt)) {
            throw new InvalidResolutionDateException(id);
        }
    }

    public static Incident declare(Equipment equipment, String description, LocalDateTime openedAt) {
        return new Incident(null, equipment, description, openedAt, null, null, null);
    }

    public boolean isResolved() {
        return resolvedAt != null;
    }

    public boolean isEvaluated() {
        return rating != null;
    }

    public boolean isAwaitingEvaluation() {
        return isResolved() && provider != null && !isEvaluated();
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

    public Optional<Duration> resolutionTime() {
        return isResolved() ? Optional.of(Duration.between(openedAt, resolvedAt)) : Optional.empty();
    }

    public AttentionLevel attentionLevel(LocalDateTime now) {
        if (provider != null) {
            return AttentionLevel.ASSIGNED;
        }
        Duration openDuration = openDuration(now);
        if (openDuration.compareTo(RED_ATTENTION_THRESHOLD) >= 0) {
            return AttentionLevel.RED;
        }
        if (openDuration.compareTo(ORANGE_ATTENTION_THRESHOLD) >= 0) {
            return AttentionLevel.ORANGE;
        }
        return AttentionLevel.GREEN;
    }

    public Incident withId(UUID persistedId) {
        return new Incident(persistedId, equipment, description, openedAt, provider, resolvedAt, rating);
    }

    public Incident withDescription(String newDescription) {
        ensureNotResolved();
        return new Incident(id, equipment, newDescription, openedAt, provider, resolvedAt, rating);
    }

    public Incident assignTo(Provider newProvider) {
        ensureNotResolved();
        return new Incident(id, equipment, description, openedAt, newProvider, resolvedAt, rating);
    }

    public Incident resolve(LocalDateTime resolutionTime) {
        ensureNotResolved();
        return new Incident(id, equipment, description, openedAt, provider, resolutionTime, rating);
    }

    public Incident evaluate(Rating newRating) {
        ensureEvaluable();
        return new Incident(id, equipment, description, openedAt, provider, resolvedAt, newRating);
    }

    private void ensureNotResolved() {
        if (isResolved()) {
            throw new IncidentAlreadyResolvedException(id);
        }
    }

    private void ensureEvaluable() {
        if (!isResolved()) {
            throw new IncidentNotResolvedException(id);
        }
        if (provider == null) {
            throw new IncidentWithoutProviderException(id);
        }
        if (isEvaluated()) {
            throw new IncidentAlreadyEvaluatedException(id);
        }
    }
}
