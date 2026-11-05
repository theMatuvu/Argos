package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.ProviderNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderDetails;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindProviderDetailsServiceTest {

    private final ProviderRepositoryPort providerRepository = mock(ProviderRepositoryPort.class);
    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final FindProviderDetailsService service = new FindProviderDetailsService(providerRepository, incidentRepository, fixedClock());

    private final Provider provider = provider();

    @Test
    void listsTheResolvedIncidentsMostRecentlyResolvedFirst() {
        Incident oldest = resolvedIncident(provider, NOW.minusDays(10));
        Incident newest = resolvedIncident(provider, NOW.minusDays(1));
        Incident middle = evaluatedIncident(provider, 4, NOW.minusDays(5));
        givenResolvedIncidents(oldest, newest, middle);

        ProviderDetails details = service.find(provider.id());

        assertEquals(provider, details.provider());
        assertEquals(List.of(newest.id(), middle.id(), oldest.id()),
                details.resolvedIncidents().stream().map(incident -> incident.incident().id()).toList());
        assertEquals(1, details.rating().evaluationCount());
    }

    @Test
    void flagsEveryIncidentWhenTheProviderIsUnderSurveillance() {
        givenResolvedIncidents(
                evaluatedIncident(provider, 2, NOW.minusDays(5)),
                evaluatedIncident(provider, 2, NOW.minusDays(4)),
                evaluatedIncident(provider, 2, NOW.minusDays(3)),
                evaluatedIncident(provider, 2, NOW.minusDays(2)),
                evaluatedIncident(provider, 2, NOW.minusDays(1))
        );

        ProviderDetails details = service.find(provider.id());

        assertTrue(details.surveillance().isUnderSurveillance());
        assertTrue(details.resolvedIncidents().stream().allMatch(IncidentDetails::providerUnderSurveillance));
    }

    @Test
    void failsWhenTheProviderDoesNotExist() {
        UUID providerId = UUID.randomUUID();
        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        assertThrows(ProviderNotFoundException.class, () -> service.find(providerId));
    }

    private void givenResolvedIncidents(Incident... incidents) {
        when(providerRepository.findById(provider.id())).thenReturn(Optional.of(provider));
        when(incidentRepository.findAllResolvedByProviderId(provider.id())).thenReturn(List.of(incidents));
    }
}
