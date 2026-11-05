package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentAlreadyResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateIncidentDescriptionServiceTest {

    private static final String NEW_DESCRIPTION = "Portique bloqué, lecteur de passe hors service.";

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final UpdateIncidentDescriptionService service = new UpdateIncidentDescriptionService(incidentRepository,
            new ProviderSurveillanceCalculator(incidentRepository), fixedClock());

    @Test
    void savesTheNewDescription() {
        Incident incident = activeIncident(NOW.minusHours(1));
        when(incidentRepository.findById(incident.id())).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(incidentRepository.findAllResolvedByProviderId(any())).thenReturn(List.of());

        IncidentDetails details = service.update(incident.id(), NEW_DESCRIPTION);

        assertEquals(NEW_DESCRIPTION, details.incident().description());
    }

    @Test
    void refusesToUpdateAResolvedIncident() {
        Incident resolved = resolvedIncident(provider(), NOW);
        when(incidentRepository.findById(resolved.id())).thenReturn(Optional.of(resolved));

        assertThrows(IncidentAlreadyResolvedException.class, () -> service.update(resolved.id(), NEW_DESCRIPTION));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void failsWhenTheIncidentDoesNotExist() {
        UUID incidentId = UUID.randomUUID();
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.update(incidentId, NEW_DESCRIPTION));
    }
}
