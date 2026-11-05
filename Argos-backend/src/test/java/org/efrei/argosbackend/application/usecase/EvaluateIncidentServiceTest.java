package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.exception.IncidentNotResolvedException;
import org.efrei.argosbackend.domain.exception.InvalidRatingException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.assignedIncident;
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

class EvaluateIncidentServiceTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final EvaluateIncidentService service = new EvaluateIncidentService(incidentRepository,
            new ProviderSurveillanceCalculator(incidentRepository), fixedClock());

    private final Provider provider = provider();

    @Test
    void savesTheRatingWithTheCurrentTimeAsEvaluationDate() {
        Incident resolved = resolvedIncident(provider, NOW.minusDays(1));
        when(incidentRepository.findById(resolved.id())).thenReturn(Optional.of(resolved));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(incidentRepository.findAllResolvedByProviderId(provider.id())).thenReturn(List.of());

        IncidentDetails details = service.evaluate(resolved.id(), 4);

        assertEquals(new Rating(4), details.incident().rating());
        assertEquals(NOW, details.incident().evaluatedAt());
    }

    @Test
    void refusesARatingOutOfRange() {
        Incident resolved = resolvedIncident(provider, NOW.minusDays(1));
        when(incidentRepository.findById(resolved.id())).thenReturn(Optional.of(resolved));

        assertThrows(InvalidRatingException.class, () -> service.evaluate(resolved.id(), Rating.MAX_VALUE + 1));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void refusesToEvaluateAnActiveIncident() {
        Incident active = assignedIncident(provider, NOW.minusHours(2));
        when(incidentRepository.findById(active.id())).thenReturn(Optional.of(active));

        assertThrows(IncidentNotResolvedException.class, () -> service.evaluate(active.id(), 4));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void failsWhenTheIncidentDoesNotExist() {
        UUID incidentId = UUID.randomUUID();
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.evaluate(incidentId, 4));
    }
}
