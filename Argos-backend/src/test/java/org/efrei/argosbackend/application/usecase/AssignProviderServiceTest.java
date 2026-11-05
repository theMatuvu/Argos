package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentAlreadyResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.exception.ProviderNotFoundException;
import org.efrei.argosbackend.domain.exception.ProviderUnderSurveillanceException;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.in.AssignProviderCommand;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignProviderServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 5, 9, 0);
    private static final Equipment EQUIPMENT = new Equipment(UUID.randomUUID(), "PTQ-TEST-1", "Portique",
            EquipmentType.TICKET_GATE, new Station(UUID.randomUUID(), "Station test", 48.85, 2.35), OperatingStatus.OUT_OF_SERVICE);
    private static final Provider PROVIDER = new Provider(UUID.randomUUID(), "Dupont Réseaux", "01 00 00 00 00");

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final ProviderRepositoryPort providerRepository = mock(ProviderRepositoryPort.class);
    private final AssignProviderService service = new AssignProviderService(incidentRepository, providerRepository,
            new ProviderSurveillanceCalculator(incidentRepository), Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

    private final Incident activeIncident = Incident.declare(EQUIPMENT, "Portique bloqué en position fermée.", NOW.minusHours(3))
            .withId(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        when(incidentRepository.findById(activeIncident.id())).thenReturn(Optional.of(activeIncident));
        when(providerRepository.findById(PROVIDER.id())).thenReturn(Optional.of(PROVIDER));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void refusesWithoutConfirmationAndSavesNothingWhenProviderIsUnderSurveillance() {
        givenProviderRatings(3, 3, 3, 3, 2);

        assertThrows(ProviderUnderSurveillanceException.class,
                () -> service.assign(command(false)));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void assignsAfterConfirmationWhenProviderIsUnderSurveillance() {
        givenProviderRatings(3, 3, 3, 3, 2);

        IncidentDetails details = service.assign(command(true));

        assertEquals(PROVIDER, details.incident().provider());
        assertTrue(details.providerUnderSurveillance());
        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void assignsWithoutConfirmationWhenProviderIsNotUnderSurveillance() {
        givenProviderRatings(3, 3, 3, 3, 3);

        IncidentDetails details = service.assign(command(false));

        assertEquals(PROVIDER, details.incident().provider());
        assertFalse(details.providerUnderSurveillance());
        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void failsWhenTheIncidentDoesNotExist() {
        UUID unknownIncidentId = UUID.randomUUID();
        when(incidentRepository.findById(unknownIncidentId)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class,
                () -> service.assign(new AssignProviderCommand(unknownIncidentId, PROVIDER.id(), false)));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void failsWhenTheProviderDoesNotExist() {
        UUID unknownProviderId = UUID.randomUUID();
        when(providerRepository.findById(unknownProviderId)).thenReturn(Optional.empty());

        assertThrows(ProviderNotFoundException.class,
                () -> service.assign(new AssignProviderCommand(activeIncident.id(), unknownProviderId, false)));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void refusesToAssignAResolvedIncident() {
        Incident resolved = activeIncident.resolve(NOW);
        when(incidentRepository.findById(resolved.id())).thenReturn(Optional.of(resolved));
        givenProviderRatings(4, 4, 4, 4, 4);

        assertThrows(IncidentAlreadyResolvedException.class,
                () -> service.assign(new AssignProviderCommand(resolved.id(), PROVIDER.id(), false)));
        verify(incidentRepository, never()).save(any());
    }

    private AssignProviderCommand command(boolean surveillanceConfirmed) {
        return new AssignProviderCommand(activeIncident.id(), PROVIDER.id(), surveillanceConfirmed);
    }

    private void givenProviderRatings(int... ratingsFromOldestToMostRecent) {
        List<Incident> evaluatedIncidents = IntStream.range(0, ratingsFromOldestToMostRecent.length)
                .mapToObj(index -> {
                    LocalDateTime resolvedAt = NOW.minusDays(30 - index);
                    return new Incident(UUID.randomUUID(), EQUIPMENT, "Incident de test évalué.", resolvedAt.minusDays(1),
                            PROVIDER, resolvedAt, new Rating(ratingsFromOldestToMostRecent[index]), resolvedAt.plusHours(2));
                })
                .toList();
        when(incidentRepository.findAllResolvedByProviderId(PROVIDER.id())).thenReturn(evaluatedIncidents);
    }
}
