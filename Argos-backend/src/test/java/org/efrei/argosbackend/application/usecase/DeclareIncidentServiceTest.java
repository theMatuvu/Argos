package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.EquipmentNotFoundException;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.efrei.argosbackend.domain.model.enums.IncidentStatus;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.in.DeclareIncidentCommand;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.DESCRIPTION;
import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.equipment;
import static org.efrei.argosbackend.TestData.fixedClock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeclareIncidentServiceTest {

    private final EquipmentRepositoryPort equipmentRepository = mock(EquipmentRepositoryPort.class);
    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final DeclareIncidentService service = new DeclareIncidentService(equipmentRepository, incidentRepository, fixedClock());

    @Test
    void savesAnIncidentOpenedNowAndPutsTheEquipmentOutOfService() {
        Equipment equipment = equipment();
        when(equipmentRepository.findById(equipment.id())).thenReturn(Optional.of(equipment));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IncidentDetails details = service.declare(new DeclareIncidentCommand(equipment.id(), DESCRIPTION));

        Incident incident = details.incident();
        assertEquals(equipment, incident.equipment());
        assertEquals(DESCRIPTION, incident.description());
        assertEquals(NOW, incident.openedAt());
        assertEquals(IncidentStatus.UNASSIGNED, incident.status());
        assertEquals(AttentionLevel.GREEN, details.attentionLevel());
        assertFalse(details.providerUnderSurveillance());
        verify(equipmentRepository).updateOperatingStatus(equipment.id(), OperatingStatus.OUT_OF_SERVICE);
    }

    @Test
    void failsWhenTheEquipmentDoesNotExist() {
        UUID equipmentId = UUID.randomUUID();
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> service.declare(new DeclareIncidentCommand(equipmentId, DESCRIPTION)));
        verify(incidentRepository, never()).save(any());
        verify(equipmentRepository, never()).updateOperatingStatus(any(), any());
    }
}
