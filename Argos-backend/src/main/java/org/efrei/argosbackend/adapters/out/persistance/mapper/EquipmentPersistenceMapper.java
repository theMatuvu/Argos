package org.efrei.argosbackend.adapters.out.persistance.mapper;

import org.efrei.argosbackend.adapters.out.persistance.entity.EquipmentEntity;
import org.efrei.argosbackend.domain.model.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentPersistenceMapper {

    private final StationPersistenceMapper stationPersistenceMapper;

    public EquipmentPersistenceMapper(StationPersistenceMapper stationPersistenceMapper) {
        this.stationPersistenceMapper = stationPersistenceMapper;
    }

    public Equipment toDomain(EquipmentEntity equipment) {
        return new Equipment(
                equipment.getId(),
                equipment.getReference(),
                equipment.getLabel(),
                equipment.getType(),
                stationPersistenceMapper.toDomain(equipment.getStation()),
                equipment.getOperatingStatus()
        );
    }
}
