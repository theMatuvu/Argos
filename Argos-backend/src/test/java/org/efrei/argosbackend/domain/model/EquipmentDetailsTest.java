package org.efrei.argosbackend.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.equipment;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentDetailsTest {

    @Test
    void separatesActiveIncidentsFromPastIncidents() {
        Incident active = activeIncident(NOW);
        Incident resolved = resolvedIncident(provider(), NOW.minusDays(2));

        EquipmentDetails details = new EquipmentDetails(equipment(), List.of(active, resolved));

        assertEquals(List.of(active), details.activeIncidents());
        assertEquals(List.of(resolved), details.pastIncidents());
    }
}
