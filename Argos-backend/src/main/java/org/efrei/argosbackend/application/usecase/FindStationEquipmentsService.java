package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.StationNotFoundException;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.model.StationEquipments;
import org.efrei.argosbackend.domain.port.in.FindStationEquipmentsUseCase;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindStationEquipmentsService implements FindStationEquipmentsUseCase {

    private final StationRepositoryPort stationRepository;
    private final EquipmentRepositoryPort equipmentRepository;

    public FindStationEquipmentsService(StationRepositoryPort stationRepository, EquipmentRepositoryPort equipmentRepository) {
        this.stationRepository = stationRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public StationEquipments find(UUID stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(stationId));
        return new StationEquipments(station, equipmentRepository.findAllByStationId(stationId));
    }
}
