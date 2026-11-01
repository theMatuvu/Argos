package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.mapper.IncidentPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.IncidentJpaRepository;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class IncidentPersistenceAdapter implements IncidentRepositoryPort {

    private final IncidentJpaRepository incidentJpaRepository;
    private final IncidentPersistenceMapper incidentPersistenceMapper;

    public IncidentPersistenceAdapter(IncidentJpaRepository incidentJpaRepository, IncidentPersistenceMapper incidentPersistenceMapper) {
        this.incidentJpaRepository = incidentJpaRepository;
        this.incidentPersistenceMapper = incidentPersistenceMapper;
    }

    @Override
    public List<Incident> findAllByEquipmentId(UUID equipmentId) {
        return incidentJpaRepository.findAllByEquipmentId(equipmentId).stream()
                .map(incidentPersistenceMapper::toDomain)
                .toList();
    }
}
