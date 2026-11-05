package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.DailyIncidentActivity;
import org.efrei.argosbackend.domain.model.EquipmentTypeStatistics;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.NetworkStatistics;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.model.StationIncidentCount;
import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentStatisticsRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.assignedIncident;
import static org.efrei.argosbackend.TestData.equipment;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.efrei.argosbackend.TestData.provider;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoadStatisticsServiceTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final IncidentStatisticsRepositoryPort incidentStatisticsRepository = mock(IncidentStatisticsRepositoryPort.class);
    private final LoadStatisticsService service = new LoadStatisticsService(incidentRepository, incidentStatisticsRepository, fixedClock());

    private final Provider provider = provider();

    @BeforeEach
    void setUp() {
        when(incidentRepository.findAllActive()).thenReturn(List.of());
        when(incidentRepository.findAllResolved()).thenReturn(List.of());
        when(incidentStatisticsRepository.countIncidentsByEquipmentType()).thenReturn(Map.of());
        when(incidentStatisticsRepository.findMostAffectedStations(anyInt())).thenReturn(List.of());
    }

    @Test
    void countsActiveIncidentsByAttentionLevelIncludingEmptyLevels() {
        when(incidentRepository.findAllActive()).thenReturn(List.of(
                activeIncident(NOW.minusHours(1)),
                activeIncident(NOW.minusHours(2)),
                activeIncident(NOW.minusHours(80)),
                assignedIncident(provider, NOW.minusHours(10))
        ));

        Map<AttentionLevel, Long> counts = service.load().activeIncidentsByAttention();

        assertEquals(Map.of(AttentionLevel.GREEN, 2L, AttentionLevel.ORANGE, 0L, AttentionLevel.RED, 1L, AttentionLevel.ASSIGNED, 1L), counts);
    }

    @Test
    void reportsTheDailyActivityOfTheLastThirtyDays() {
        Incident openedAndResolvedToday = activeIncident(NOW.minusHours(3)).resolve(NOW.minusHours(1));
        Incident openedTwoDaysAgo = activeIncident(NOW.minusDays(2));
        Incident openedLongAgo = activeIncident(NOW.minusDays(40)).resolve(NOW.minusDays(39));
        when(incidentRepository.findAllActive()).thenReturn(List.of(openedTwoDaysAgo));
        when(incidentRepository.findAllResolved()).thenReturn(List.of(openedAndResolvedToday, openedLongAgo));

        NetworkStatistics statistics = service.load();
        List<DailyIncidentActivity> activity = statistics.dailyActivity();

        LocalDate today = NOW.toLocalDate();
        assertEquals(30, activity.size());
        assertEquals(today.minusDays(29), activity.getFirst().day());
        assertEquals(new DailyIncidentActivity(today, 1, 1), activity.getLast());
        assertEquals(new DailyIncidentActivity(today.minusDays(2), 1, 0), activity.get(27));
        assertEquals(2, statistics.openedIncidentCount());
        assertEquals(1, statistics.resolvedIncidentCount());
    }

    @Test
    void computesTheStatisticsOfEveryEquipmentType() {
        Incident elevatorResolvedInTwoHours = activeIncident(equipment(EquipmentType.ELEVATOR), NOW.minusHours(5)).resolve(NOW.minusHours(3));
        Incident elevatorResolvedInFourHours = activeIncident(equipment(EquipmentType.ELEVATOR), NOW.minusHours(6)).resolve(NOW.minusHours(2));
        when(incidentRepository.findAllResolved()).thenReturn(List.of(elevatorResolvedInTwoHours, elevatorResolvedInFourHours));
        when(incidentStatisticsRepository.countIncidentsByEquipmentType()).thenReturn(Map.of(EquipmentType.ELEVATOR, 3L));

        NetworkStatistics statistics = service.load();

        assertEquals(EquipmentType.values().length, statistics.equipmentTypes().size());
        EquipmentTypeStatistics elevators = statistics.equipmentTypes().getFirst();
        assertEquals(EquipmentType.ELEVATOR, elevators.type());
        assertEquals(3, elevators.incidentCount());
        assertEquals(180.0, elevators.averageResolutionMinutes().getAsDouble(), 1e-9);
        EquipmentTypeStatistics escalators = statistics.equipmentTypes().get(1);
        assertEquals(0, escalators.incidentCount());
        assertTrue(escalators.averageResolutionMinutes().isEmpty());
        assertEquals(180.0, statistics.averageResolutionMinutes().getAsDouble(), 1e-9);
    }

    @Test
    void averagesTheRatingsOfEvaluatedIncidents() {
        Incident evaluated = assignedIncident(provider, NOW.minusDays(2)).resolve(NOW.minusDays(1))
                .evaluate(new Rating(4), NOW);
        Incident notEvaluated = assignedIncident(provider, NOW.minusDays(2)).resolve(NOW.minusDays(1));
        when(incidentRepository.findAllResolved()).thenReturn(List.of(evaluated, notEvaluated));

        NetworkStatistics statistics = service.load();

        assertEquals(1, statistics.providerRating().evaluationCount());
        assertEquals(4.0, statistics.providerRating().average().getAsDouble(), 1e-9);
    }

    @Test
    void asksForTheEightMostAffectedStations() {
        List<StationIncidentCount> stations = List.of(new StationIncidentCount("Châtelet", 5));
        when(incidentStatisticsRepository.findMostAffectedStations(8)).thenReturn(stations);

        assertEquals(stations, service.load().mostAffectedStations());
        verify(incidentStatisticsRepository).findMostAffectedStations(8);
    }
}
