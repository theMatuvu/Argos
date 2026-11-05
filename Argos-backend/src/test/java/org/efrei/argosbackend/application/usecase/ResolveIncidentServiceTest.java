package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentAlreadyResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResolveIncidentServiceTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final EquipmentRepositoryPort equipmentRepository = mock(EquipmentRepositoryPort.class);
    private final ResolveIncidentService service = new ResolveIncidentService(incidentRepository, equipmentRepository,
            new ProviderSurveillanceCalculator(incidentRepository), fixedClock());

    private final Provider provider = provider();
    private final Incident incident = assignedIncident(provider, NOW.minusHours(6));

    @Test
    void resolvesNowAndPutsTheEquipmentBackInServiceWhenNoOtherIncidentIsActive() {
        givenIncidentCanBeSaved();
        when(incidentRepository.existsActiveByEquipmentId(incident.equipment().id())).thenReturn(false);

        IncidentDetails details = service.resolve(incident.id());

        assertEquals(NOW, details.incident().resolvedAt());
        assertNull(details.attentionLevel());
        verify(equipmentRepository).updateOperatingStatus(incident.equipment().id(), OperatingStatus.IN_SERVICE);
    }

    @Test
    void leavesTheEquipmentOutOfServiceWhileAnotherIncidentIsActive() {
        givenIncidentCanBeSaved();
        when(incidentRepository.existsActiveByEquipmentId(incident.equipment().id())).thenReturn(true);

        service.resolve(incident.id());

        verify(equipmentRepository, never()).updateOperatingStatus(any(), any());
    }

    @Test
    void refusesToResolveAnIncidentTwice() {
        Incident resolved = resolvedIncident(provider, NOW.minusHours(1));
        when(incidentRepository.findById(resolved.id())).thenReturn(Optional.of(resolved));

        assertThrows(IncidentAlreadyResolvedException.class, () -> service.resolve(resolved.id()));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void failsWhenTheIncidentDoesNotExist() {
        UUID incidentId = UUID.randomUUID();
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.resolve(incidentId));
    }

    private void givenIncidentCanBeSaved() {
        when(incidentRepository.findById(incident.id())).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(incidentRepository.findAllResolvedByProviderId(provider.id())).thenReturn(List.of());
    }
}
