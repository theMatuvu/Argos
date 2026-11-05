package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Line;
import org.efrei.argosbackend.domain.model.LineSegment;
import org.efrei.argosbackend.domain.model.NetworkMap;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.port.out.LineRepositoryPort;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.station;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoadNetworkMapServiceTest {

    private final StationRepositoryPort stationRepository = mock(StationRepositoryPort.class);
    private final LineRepositoryPort lineRepository = mock(LineRepositoryPort.class);
    private final LoadNetworkMapService service = new LoadNetworkMapService(stationRepository, lineRepository);

    @Test
    void assemblesTheStationsAndTheLines() {
        Station chatelet = station("Châtelet");
        Station nation = station("Nation");
        Line line = new Line(UUID.randomUUID(), "1", "#FFCD00", "#000000", List.of(new LineSegment(chatelet, nation)));
        when(stationRepository.findAll()).thenReturn(List.of(chatelet, nation));
        when(lineRepository.findAll()).thenReturn(List.of(line));

        NetworkMap map = service.load();

        assertEquals(List.of(chatelet, nation), map.stations());
        assertEquals(List.of(line), map.lines());
    }
}
