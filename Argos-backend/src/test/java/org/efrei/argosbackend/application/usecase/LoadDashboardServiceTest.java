package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Dashboard;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.assignedIncident;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoadDashboardServiceTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final EquipmentRepositoryPort equipmentRepository = mock(EquipmentRepositoryPort.class);
    private final LoadDashboardService service = new LoadDashboardService(incidentRepository, equipmentRepository,
            new ProviderSurveillanceCalculator(incidentRepository), fixedClock());

    private final Provider provider = provider();

    @Test
    void listsActiveIncidentsOldestFirstWithTheirAttentionLevel() {
        Incident recent = activeIncident(NOW.minusHours(2));
        Incident old = activeIncident(NOW.minusHours(80));
        Incident assigned = assignedIncident(provider, NOW.minusHours(50));
        when(incidentRepository.findAllActive()).thenReturn(List.of(recent, old, assigned));
        when(incidentRepository.findAllResolved()).thenReturn(List.of());

        Dashboard dashboard = service.load();

        assertEquals(List.of(old.id(), assigned.id(), recent.id()), ids(dashboard.activeIncidents()));
        assertEquals(List.of(AttentionLevel.RED, AttentionLevel.ASSIGNED, AttentionLevel.GREEN),
                dashboard.activeIncidents().stream().map(IncidentDetails::attentionLevel).toList());
        assertEquals(2, dashboard.unassignedCount());
        assertEquals(1, dashboard.assignedCount());
    }

    @Test
    void listsOnlyResolvedIncidentsAwaitingEvaluationMostRecentlyResolvedFirst() {
        Incident olderAwaiting = resolvedIncident(provider, NOW.minusDays(3));
        Incident newerAwaiting = resolvedIncident(provider, NOW.minusDays(1));
        Incident evaluated = evaluatedIncident(provider, 4, NOW.minusDays(2));
        Incident withoutProvider = activeIncident(NOW.minusDays(2)).resolve(NOW.minusDays(1));
        when(incidentRepository.findAllActive()).thenReturn(List.of());
        when(incidentRepository.findAllResolved()).thenReturn(List.of(olderAwaiting, evaluated, withoutProvider, newerAwaiting));

        Dashboard dashboard = service.load();

        assertEquals(List.of(newerAwaiting.id(), olderAwaiting.id()), ids(dashboard.incidentsAwaitingEvaluation()));
    }

    @Test
    void countsTheEquipmentsOutOfService() {
        when(incidentRepository.findAllActive()).thenReturn(List.of());
        when(incidentRepository.findAllResolved()).thenReturn(List.of());
        when(equipmentRepository.countByOperatingStatus(OperatingStatus.OUT_OF_SERVICE)).thenReturn(7L);

        assertEquals(7, service.load().outOfServiceEquipmentCount());
    }

    private static List<UUID> ids(List<IncidentDetails> details) {
        return details.stream().map(detail -> detail.incident().id()).toList();
    }
}
