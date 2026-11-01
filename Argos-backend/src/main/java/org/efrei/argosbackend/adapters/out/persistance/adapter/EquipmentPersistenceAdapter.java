package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.entity.EquipmentEntity;
import org.efrei.argosbackend.adapters.out.persistance.mapper.EquipmentPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.EquipmentJpaRepository;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipmentPersistenceAdapter implements EquipmentRepositoryPort {

    private final EquipmentJpaRepository equipmentJpaRepository;
    private final EquipmentPersistenceMapper equipmentPersistenceMapper;

    public EquipmentPersistenceAdapter(EquipmentJpaRepository equipmentJpaRepository, EquipmentPersistenceMapper equipmentPersistenceMapper) {
        this.equipmentJpaRepository = equipmentJpaRepository;
        this.equipmentPersistenceMapper = equipmentPersistenceMapper;
    }

    @Override
    public List<Equipment> findAllByStation(String stationName) {
        equipmentJpaRepository.findAllByStation_Name(stationName);
        return equipmentJpaRepository.findAllByStation_Name(stationName).stream()
                .map(equipmentPersistenceMapper::toDomain)
                .toList();

    }
}
