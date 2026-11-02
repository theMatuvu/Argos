package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.IncidentDetails;

import java.util.UUID;

public interface ResolveIncidentUseCase {
    IncidentDetails resolve(UUID incidentId);
}
