package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.efrei.argosbackend.TestData.station;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListStationsServiceTest {

    private final StationRepositoryPort stationRepository = mock(StationRepositoryPort.class);
    private final ListStationsService service = new ListStationsService(stationRepository);

    @Test
    void returnsEveryStation() {
        List<Station> stations = List.of(station("Châtelet"), station("Nation"));
        when(stationRepository.findAll()).thenReturn(stations);

        assertEquals(stations, service.list());
    }
}
