package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.Incident;

import java.util.List;
import java.util.UUID;

public interface IncidentRepositoryPort {
    List<Incident> findAllByEquipmentId(UUID equipmentId);
}
