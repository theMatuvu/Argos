package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.adapters.out.persistance.entity.EquipmentEntity;
import org.efrei.argosbackend.domain.model.Equipment;

import java.util.List;

public interface EquipmentRepositoryPort {

    List<Equipment> findAllByStation(String stationName);

}
