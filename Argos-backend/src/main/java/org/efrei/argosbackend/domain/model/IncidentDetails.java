package org.efrei.argosbackend.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record IncidentDetails(Incident incident, Duration openDuration) {

    public static IncidentDetails of(Incident incident, LocalDateTime now) {
        return new IncidentDetails(incident, incident.openDuration(now));
    }
}
