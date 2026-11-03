package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.Incident;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepositoryPort {
    List<Incident> findAllByEquipmentId(UUID equipmentId);

    Optional<Incident> findById(UUID incidentId);

    List<Incident> findAllResolved();

    List<Incident> findAllResolvedByProviderId(UUID providerId);

    boolean existsActiveByEquipmentId(UUID equipmentId);

    Incident save(Incident incident);
}
