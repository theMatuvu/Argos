package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.mapper.EquipmentPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.EquipmentJpaRepository;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EquipmentPersistenceAdapter implements EquipmentRepositoryPort {

    private final EquipmentJpaRepository equipmentJpaRepository;
    private final EquipmentPersistenceMapper equipmentPersistenceMapper;

    public EquipmentPersistenceAdapter(EquipmentJpaRepository equipmentJpaRepository, EquipmentPersistenceMapper equipmentPersistenceMapper) {
        this.equipmentJpaRepository = equipmentJpaRepository;
        this.equipmentPersistenceMapper = equipmentPersistenceMapper;
    }

    @Override
    public List<Equipment> findAllByStationId(UUID stationId) {
        return equipmentJpaRepository.findAllByStationId(stationId).stream()
                .map(equipmentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Equipment> findById(UUID equipmentId) {
        return equipmentJpaRepository.findWithStationById(equipmentId).map(equipmentPersistenceMapper::toDomain);
    }
}
