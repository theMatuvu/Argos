package org.efrei.argosbackend.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.assignedIncident;
import static org.efrei.argosbackend.TestData.provider;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DashboardTest {

    @Test
    void countsActiveIncidentsByStatus() {
        Provider provider = provider();
        Dashboard dashboard = new Dashboard(List.of(
                details(activeIncident(NOW.minusHours(1))),
                details(activeIncident(NOW.minusHours(2))),
                details(assignedIncident(provider, NOW.minusHours(3)))
        ), List.of(), 4);

        assertEquals(2, dashboard.unassignedCount());
        assertEquals(1, dashboard.assignedCount());
        assertEquals(4, dashboard.outOfServiceEquipmentCount());
    }

    @Test
    void keepsAnImmutableCopyOfTheLists() {
        List<IncidentDetails> activeIncidents = new ArrayList<>(List.of(details(activeIncident(NOW))));
        Dashboard dashboard = new Dashboard(activeIncidents, List.of(), 0);

        activeIncidents.clear();

        assertEquals(1, dashboard.activeIncidents().size());
        assertThrows(UnsupportedOperationException.class, () -> dashboard.activeIncidents().clear());
    }

    private static IncidentDetails details(Incident incident) {
        return IncidentDetails.of(incident, NOW, ProviderSurveillance.none());
    }
}
