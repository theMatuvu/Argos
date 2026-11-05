package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.assignedIncident;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentDetailsTest {

    private final Provider provider = provider();
    private final ProviderSurveillance underSurveillance = ProviderSurveillance.of(List.of(
            evaluatedIncident(provider, 1, NOW.minusDays(5)),
            evaluatedIncident(provider, 2, NOW.minusDays(4)),
            evaluatedIncident(provider, 2, NOW.minusDays(3)),
            evaluatedIncident(provider, 2, NOW.minusDays(2)),
            evaluatedIncident(provider, 2, NOW.minusDays(1))
    ));

    @Test
    void computesTheOpenDurationAndAttentionLevelOfAnActiveIncident() {
        IncidentDetails details = IncidentDetails.of(activeIncident(NOW.minusHours(50)), NOW, ProviderSurveillance.none());

        assertEquals(Duration.ofHours(50), details.openDuration());
        assertEquals(AttentionLevel.ORANGE, details.attentionLevel());
    }

    @Test
    void hasNoAttentionLevelOnceResolved() {
        IncidentDetails details = IncidentDetails.of(resolvedIncident(provider, NOW.minusHours(1)), NOW, ProviderSurveillance.none());

        assertNull(details.attentionLevel());
    }

    @Test
    void flagsAnIncidentAssignedToAProviderUnderSurveillance() {
        IncidentDetails details = IncidentDetails.of(assignedIncident(provider, NOW), NOW, underSurveillance);

        assertTrue(details.providerUnderSurveillance());
    }

    @Test
    void doesNotFlagAnIncidentWithoutProvider() {
        IncidentDetails details = IncidentDetails.of(activeIncident(NOW), NOW, underSurveillance);

        assertFalse(details.providerUnderSurveillance());
    }

    @Test
    void doesNotFlagAnIncidentAssignedToAProviderNotUnderSurveillance() {
        IncidentDetails details = IncidentDetails.of(assignedIncident(provider, NOW), NOW, ProviderSurveillance.none());

        assertFalse(details.providerUnderSurveillance());
    }
}
