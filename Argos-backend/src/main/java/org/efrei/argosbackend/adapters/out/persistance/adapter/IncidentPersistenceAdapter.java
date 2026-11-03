package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.entity.IncidentEntity;
import org.efrei.argosbackend.adapters.out.persistance.entity.ProviderEntity;
import org.efrei.argosbackend.adapters.out.persistance.mapper.IncidentPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.EquipmentJpaRepository;
import org.efrei.argosbackend.adapters.out.persistance.repository.IncidentJpaRepository;
import org.efrei.argosbackend.adapters.out.persistance.repository.ProviderJpaRepository;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class IncidentPersistenceAdapter implements IncidentRepositoryPort {

    private final IncidentJpaRepository incidentJpaRepository;
    private final EquipmentJpaRepository equipmentJpaRepository;
    private final ProviderJpaRepository providerJpaRepository;
    private final IncidentPersistenceMapper incidentPersistenceMapper;

    public IncidentPersistenceAdapter(IncidentJpaRepository incidentJpaRepository, EquipmentJpaRepository equipmentJpaRepository,
                                      ProviderJpaRepository providerJpaRepository, IncidentPersistenceMapper incidentPersistenceMapper) {
        this.incidentJpaRepository = incidentJpaRepository;
        this.equipmentJpaRepository = equipmentJpaRepository;
        this.providerJpaRepository = providerJpaRepository;
        this.incidentPersistenceMapper = incidentPersistenceMapper;
    }

    @Override
    public List<Incident> findAllByEquipmentId(UUID equipmentId) {
        return toDomain(incidentJpaRepository.findAllByEquipmentId(equipmentId));
    }

    @Override
    public Optional<Incident> findById(UUID incidentId) {
        return incidentJpaRepository.findWithDetailsById(incidentId).map(incidentPersistenceMapper::toDomain);
    }

    @Override
    public List<Incident> findAllResolved() {
        return toDomain(incidentJpaRepository.findAllResolved());
    }

    @Override
    public List<Incident> findAllResolvedByProviderId(UUID providerId) {
        return toDomain(incidentJpaRepository.findAllResolvedByProviderId(providerId));
    }

    @Override
    public boolean existsActiveByEquipmentId(UUID equipmentId) {
        return incidentJpaRepository.existsByEquipmentIdAndResolvedAtIsNull(equipmentId);
    }

    @Override
    public Incident save(Incident incident) {
        IncidentEntity saved = incidentJpaRepository.save(toEntity(incident));
        return incident.withId(saved.getId());
    }

    private IncidentEntity toEntity(Incident incident) {
        return incidentPersistenceMapper.toEntity(
                incident,
                equipmentJpaRepository.getReferenceById(incident.equipment().id()),
                providerReference(incident.provider())
        );
    }

    private ProviderEntity providerReference(Provider provider) {
        return provider == null ? null : providerJpaRepository.getReferenceById(provider.id());
    }

    private List<Incident> toDomain(List<IncidentEntity> incidents) {
        return incidents.stream().map(incidentPersistenceMapper::toDomain).toList();
    }
}
