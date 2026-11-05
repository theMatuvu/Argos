package org.efrei.argosbackend.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProviderDetailsTest {

    private final Provider provider = provider();

    @Test
    void averagesOnlyTheEvaluatedIncidents() {
        ProviderDetails details = new ProviderDetails(provider, List.of(
                details(evaluatedIncident(provider, 5, NOW.minusDays(2))),
                details(evaluatedIncident(provider, 3, NOW.minusDays(1))),
                details(resolvedIncident(provider, NOW))
        ));

        assertEquals(2, details.rating().evaluationCount());
        assertEquals(4.0, details.rating().average().getAsDouble(), 1e-9);
        assertFalse(details.surveillance().isUnderSurveillance());
    }

    @Test
    void computesTheSurveillanceFromTheResolvedIncidents() {
        ProviderDetails details = new ProviderDetails(provider, List.of(
                details(evaluatedIncident(provider, 2, NOW.minusDays(5))),
                details(evaluatedIncident(provider, 3, NOW.minusDays(4))),
                details(evaluatedIncident(provider, 2, NOW.minusDays(3))),
                details(evaluatedIncident(provider, 3, NOW.minusDays(2))),
                details(evaluatedIncident(provider, 2, NOW.minusDays(1)))
        ));

        assertTrue(details.surveillance().isUnderSurveillance());
    }

    private static IncidentDetails details(Incident incident) {
        return IncidentDetails.of(incident, NOW, ProviderSurveillance.none());
    }
}
