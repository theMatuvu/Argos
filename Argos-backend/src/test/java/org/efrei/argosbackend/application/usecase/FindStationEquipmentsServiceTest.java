package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.StationNotFoundException;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.model.StationEquipments;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.equipment;
import static org.efrei.argosbackend.TestData.station;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindStationEquipmentsServiceTest {

    private final StationRepositoryPort stationRepository = mock(StationRepositoryPort.class);
    private final EquipmentRepositoryPort equipmentRepository = mock(EquipmentRepositoryPort.class);
    private final FindStationEquipmentsService service = new FindStationEquipmentsService(stationRepository, equipmentRepository);

    @Test
    void returnsTheStationWithItsEquipments() {
        Station station = station();
        List<Equipment> equipments = List.of(equipment(), equipment());
        when(stationRepository.findById(station.id())).thenReturn(Optional.of(station));
        when(equipmentRepository.findAllByStationId(station.id())).thenReturn(equipments);

        StationEquipments result = service.find(station.id());

        assertEquals(station, result.station());
        assertEquals(equipments, result.equipments());
    }

    @Test
    void failsWhenTheStationDoesNotExist() {
        UUID stationId = UUID.randomUUID();
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class, () -> service.find(stationId));
    }
}
