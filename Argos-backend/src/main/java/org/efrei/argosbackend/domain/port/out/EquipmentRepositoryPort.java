package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.Equipment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepositoryPort {
    List<Equipment> findAllByStationId(UUID stationId);

    Optional<Equipment> findById(UUID equipmentId);
}
