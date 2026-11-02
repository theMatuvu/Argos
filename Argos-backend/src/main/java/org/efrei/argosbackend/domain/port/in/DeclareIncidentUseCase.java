package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.IncidentDetails;

public interface DeclareIncidentUseCase {
    IncidentDetails declare(DeclareIncidentCommand command);
}
