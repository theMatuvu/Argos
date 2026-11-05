package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListResolvedIncidentsServiceTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final ListResolvedIncidentsService service = new ListResolvedIncidentsService(incidentRepository,
            new ProviderSurveillanceCalculator(incidentRepository), fixedClock());

    @Test
    void listsTheResolvedIncidentsMostRecentlyResolvedFirstWithTheSurveillanceOfTheirProvider() {
        Provider provider = provider();
        Incident newest = resolvedIncident(provider, NOW.minusHours(1));
        List<Incident> evaluated = List.of(
                evaluatedIncident(provider, 1, NOW.minusDays(5)),
                evaluatedIncident(provider, 2, NOW.minusDays(4)),
                evaluatedIncident(provider, 2, NOW.minusDays(3)),
                evaluatedIncident(provider, 2, NOW.minusDays(2)),
                evaluatedIncident(provider, 2, NOW.minusDays(1))
        );
        when(incidentRepository.findAllResolved()).thenReturn(List.of(evaluated.get(2), newest, evaluated.get(0),
                evaluated.get(4), evaluated.get(1), evaluated.get(3)));

        List<IncidentDetails> details = service.list();

        assertEquals(List.of(newest.id(), evaluated.get(4).id(), evaluated.get(3).id(), evaluated.get(2).id(),
                evaluated.get(1).id(), evaluated.get(0).id()), details.stream().map(detail -> detail.incident().id()).toList());
        assertTrue(details.stream().allMatch(IncidentDetails::providerUnderSurveillance));
    }
}
