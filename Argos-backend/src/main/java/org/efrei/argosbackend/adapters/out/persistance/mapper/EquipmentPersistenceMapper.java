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

    public Equipment toDomain(EquipmentEntity equipmentEntity) {
        return new Equipment(
                equipmentEntity.getId(),
                equipmentEntity.getName(),
                equipmentEntity.getReference(),
                equipmentEntity.getLabel(),
                equipmentEntity.getEquipmentType(),
                stationPersistenceMapper.toDomain(equipmentEntity.getStation()),
                equipmentEntity.isBroken()
        );
    }
}
