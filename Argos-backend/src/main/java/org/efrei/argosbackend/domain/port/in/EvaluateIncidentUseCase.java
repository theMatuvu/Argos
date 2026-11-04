package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.IncidentDetails;

import java.util.UUID;

public interface EvaluateIncidentUseCase {
    IncidentDetails evaluate(UUID incidentId, int rating);
}
