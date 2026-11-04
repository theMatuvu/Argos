package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.AttentionLevel;

import java.time.Duration;
import java.time.LocalDateTime;

public record IncidentDetails(Incident incident, Duration openDuration, AttentionLevel attentionLevel) {

    public static IncidentDetails of(Incident incident, LocalDateTime now) {
        AttentionLevel attentionLevel = incident.isResolved() ? null : incident.attentionLevel(now);
        return new IncidentDetails(incident, incident.openDuration(now), attentionLevel);
    }
}
