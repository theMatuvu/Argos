package org.efrei.argosbackend.adapters.out.persistance.mapper;

import org.efrei.argosbackend.adapters.out.persistance.entity.EquipmentEntity;
import org.efrei.argosbackend.adapters.out.persistance.entity.IncidentEntity;
import org.efrei.argosbackend.adapters.out.persistance.entity.ProviderEntity;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.Rating;
import org.springframework.stereotype.Component;

@Component
public class IncidentPersistenceMapper {

    private final EquipmentPersistenceMapper equipmentPersistenceMapper;
    private final ProviderPersistenceMapper providerPersistenceMapper;

    public IncidentPersistenceMapper(EquipmentPersistenceMapper equipmentPersistenceMapper, ProviderPersistenceMapper providerPersistenceMapper) {
        this.equipmentPersistenceMapper = equipmentPersistenceMapper;
        this.providerPersistenceMapper = providerPersistenceMapper;
    }

    public Incident toDomain(IncidentEntity incident) {
        return new Incident(
                incident.getId(),
                equipmentPersistenceMapper.toDomain(incident.getEquipment()),
                incident.getDescription(),
                incident.getOpenedAt(),
                toDomainProvider(incident),
                incident.getResolvedAt(),
                toDomainRating(incident),
                incident.getEvaluatedAt()
        );
    }

    public IncidentEntity toEntity(Incident incident, EquipmentEntity equipment, ProviderEntity provider) {
        return new IncidentEntity(
                incident.id(),
                equipment,
                incident.description(),
                incident.openedAt(),
                provider,
                incident.resolvedAt(),
                incident.isEvaluated() ? incident.rating().value() : null,
                incident.evaluatedAt()
        );
    }

    private Rating toDomainRating(IncidentEntity incident) {
        return incident.getRating() == null ? null : new Rating(incident.getRating());
    }

    private Provider toDomainProvider(IncidentEntity incident) {
        return incident.getProvider() == null ? null : providerPersistenceMapper.toDomain(incident.getProvider());
    }
}
