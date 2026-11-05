package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.assignedIncident;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.efrei.argosbackend.TestData.provider;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindIncidentDetailsServiceTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final FindIncidentDetailsService service = new FindIncidentDetailsService(incidentRepository,
            new ProviderSurveillanceCalculator(incidentRepository), fixedClock());

    @Test
    void computesTheDetailsAtTheCurrentTime() {
        Incident incident = activeIncident(NOW.minusHours(3));
        when(incidentRepository.findById(incident.id())).thenReturn(Optional.of(incident));

        IncidentDetails details = service.find(incident.id());

        assertEquals(Duration.ofHours(3), details.openDuration());
        assertFalse(details.providerUnderSurveillance());
        verify(incidentRepository, never()).findAllResolvedByProviderId(any());
    }

    @Test
    void flagsAnIncidentAssignedToAProviderUnderSurveillance() {
        Provider provider = provider();
        Incident incident = assignedIncident(provider, NOW.minusHours(3));
        when(incidentRepository.findById(incident.id())).thenReturn(Optional.of(incident));
        when(incidentRepository.findAllResolvedByProviderId(provider.id())).thenReturn(List.of(
                evaluatedIncident(provider, 1, NOW.minusDays(5)),
                evaluatedIncident(provider, 2, NOW.minusDays(4)),
                evaluatedIncident(provider, 3, NOW.minusDays(3)),
                evaluatedIncident(provider, 2, NOW.minusDays(2)),
                evaluatedIncident(provider, 2, NOW.minusDays(1))
        ));

        assertTrue(service.find(incident.id()).providerUnderSurveillance());
    }

    @Test
    void failsWhenTheIncidentDoesNotExist() {
        UUID incidentId = UUID.randomUUID();
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.find(incidentId));
    }
}
