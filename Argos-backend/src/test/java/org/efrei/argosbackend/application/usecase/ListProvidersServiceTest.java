package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderSummary;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListProvidersServiceTest {

    private final ProviderRepositoryPort providerRepository = mock(ProviderRepositoryPort.class);
    private final IncidentRepositoryPort incidentRepository = mock(IncidentRepositoryPort.class);
    private final ListProvidersService service = new ListProvidersService(providerRepository, incidentRepository);

    @Test
    void summarisesTheRatingAndSurveillanceOfEveryProvider() {
        Provider watchedProvider = provider("Prestataire surveillé");
        Provider trustedProvider = provider("Prestataire fiable");
        Provider newProvider = provider("Nouveau prestataire");
        when(providerRepository.findAll()).thenReturn(List.of(watchedProvider, trustedProvider, newProvider));
        when(incidentRepository.findAllResolved()).thenReturn(List.of(
                evaluatedIncident(watchedProvider, 2, NOW.minusDays(5)),
                evaluatedIncident(watchedProvider, 3, NOW.minusDays(4)),
                evaluatedIncident(watchedProvider, 2, NOW.minusDays(3)),
                evaluatedIncident(watchedProvider, 3, NOW.minusDays(2)),
                evaluatedIncident(watchedProvider, 2, NOW.minusDays(1)),
                evaluatedIncident(trustedProvider, 5, NOW.minusDays(1)),
                resolvedIncident(trustedProvider, NOW)
        ));

        List<ProviderSummary> summaries = service.list();

        ProviderSummary watched = summaries.get(0);
        assertEquals(5, watched.rating().evaluationCount());
        assertEquals(2.4, watched.rating().average().getAsDouble(), 1e-9);
        assertTrue(watched.surveillance().isUnderSurveillance());

        ProviderSummary trusted = summaries.get(1);
        assertEquals(1, trusted.rating().evaluationCount());
        assertEquals(5.0, trusted.rating().average().getAsDouble(), 1e-9);
        assertFalse(trusted.surveillance().isUnderSurveillance());

        ProviderSummary fresh = summaries.get(2);
        assertEquals(newProvider, fresh.provider());
        assertEquals(0, fresh.rating().evaluationCount());
        assertTrue(fresh.rating().average().isEmpty());
        assertFalse(fresh.surveillance().isUnderSurveillance());
    }
}
