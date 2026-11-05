package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.IncidentDetails;

public interface AssignProviderUseCase {
    IncidentDetails assign(AssignProviderCommand command);
}
