package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.IncidentDetails;

import java.util.List;

public interface ListResolvedIncidentsUseCase {
    List<IncidentDetails> list();
}
