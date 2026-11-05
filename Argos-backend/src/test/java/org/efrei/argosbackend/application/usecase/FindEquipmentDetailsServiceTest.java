package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.EquipmentNotFoundException;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.EquipmentDetails;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.equipment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindEquipmentDetailsServiceTest {

    private final EquipmentRepositoryPort equipmentRepository = mock(EquipmentRepositoryPort.class);
    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final FindEquipmentDetailsService service = new FindEquipmentDetailsService(equipmentRepository, incidentRepository);

    @Test
    void returnsTheEquipmentWithItsIncidentsMostRecentlyOpenedFirst() {
        Equipment equipment = equipment();
        Incident oldest = activeIncident(equipment, NOW.minusDays(3));
        Incident newest = activeIncident(equipment, NOW.minusHours(1));
        Incident middle = activeIncident(equipment, NOW.minusDays(1));
        when(equipmentRepository.findById(equipment.id())).thenReturn(Optional.of(equipment));
        when(incidentRepository.findAllByEquipmentId(equipment.id())).thenReturn(List.of(oldest, newest, middle));

        EquipmentDetails details = service.find(equipment.id());

        assertEquals(equipment, details.equipment());
        assertEquals(List.of(newest, middle, oldest), details.incidents());
    }

    @Test
    void failsWhenTheEquipmentDoesNotExist() {
        UUID equipmentId = UUID.randomUUID();
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> service.find(equipmentId));
    }
}
