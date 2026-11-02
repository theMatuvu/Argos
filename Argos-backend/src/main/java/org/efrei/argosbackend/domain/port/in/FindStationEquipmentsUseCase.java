package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.StationEquipments;

import java.util.UUID;

public interface FindStationEquipmentsUseCase {
    StationEquipments find(UUID stationId);
}
