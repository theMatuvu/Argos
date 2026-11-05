package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProviderSurveillanceCalculatorTest {

    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final ProviderSurveillanceCalculator calculator = new ProviderSurveillanceCalculator(incidentRepository);

    private final Provider watchedProvider = provider("Prestataire surveillé");
    private final Provider trustedProvider = provider("Prestataire fiable");

    @Test
    void returnsNoSurveillanceWithoutProvider() {
        assertEquals(ProviderSurveillance.none(), calculator.of(null));
        verify(incidentRepository, never()).findAllResolvedByProviderId(any());
    }

    @Test
    void computesTheSurveillanceOfOneProviderFromItsResolvedIncidents() {
        when(incidentRepository.findAllResolvedByProviderId(watchedProvider.id())).thenReturn(lowRatings(watchedProvider));

        assertTrue(calculator.of(watchedProvider).isUnderSurveillance());
    }

    @Test
    void computesTheSurveillanceOfEveryProviderWithASingleQuery() {
        List<Incident> resolvedIncidents = new ArrayList<>(lowRatings(watchedProvider));
        resolvedIncidents.add(evaluatedIncident(trustedProvider, 5, NOW.minusDays(1)));
        resolvedIncidents.add(resolvedIncident(trustedProvider, NOW));
        when(incidentRepository.findAllResolved()).thenReturn(resolvedIncidents);

        Function<Provider, ProviderSurveillance> surveillanceOf = calculator.forAllProviders();

        assertTrue(surveillanceOf.apply(watchedProvider).isUnderSurveillance());
        assertFalse(surveillanceOf.apply(trustedProvider).isUnderSurveillance());
        assertEquals(ProviderSurveillance.none(), surveillanceOf.apply(provider("Prestataire sans intervention")));
        assertEquals(ProviderSurveillance.none(), surveillanceOf.apply(null));
    }

    private static List<Incident> lowRatings(Provider provider) {
        return List.of(
                evaluatedIncident(provider, 2, NOW.minusDays(5)),
                evaluatedIncident(provider, 2, NOW.minusDays(4)),
                evaluatedIncident(provider, 3, NOW.minusDays(3)),
                evaluatedIncident(provider, 2, NOW.minusDays(2)),
                evaluatedIncident(provider, 3, NOW.minusDays(1))
        );
    }
}
