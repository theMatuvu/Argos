package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.ProviderNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderDetails;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
import org.efrei.argosbackend.domain.port.in.FindProviderDetailsUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class FindProviderDetailsService implements FindProviderDetailsUseCase {

    private static final Comparator<Incident> MOST_RECENTLY_RESOLVED_FIRST =
            Comparator.comparing(Incident::resolvedAt).reversed();

    private final ProviderRepositoryPort providerRepository;
    private final IncidentRepositoryPort incidentRepository;
    private final Clock clock;

    public FindProviderDetailsService(ProviderRepositoryPort providerRepository, IncidentRepositoryPort incidentRepository, Clock clock) {
        this.providerRepository = providerRepository;
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    @Override
    public ProviderDetails find(UUID providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException(providerId));
        return new ProviderDetails(provider, resolvedIncidents(providerId));
    }

    private List<IncidentDetails> resolvedIncidents(UUID providerId) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Incident> resolvedIncidents = incidentRepository.findAllResolvedByProviderId(providerId);
        ProviderSurveillance surveillance = ProviderSurveillance.of(resolvedIncidents);
        return resolvedIncidents.stream()
                .sorted(MOST_RECENTLY_RESOLVED_FIRST)
                .map(incident -> IncidentDetails.of(incident, now, surveillance))
                .toList();
    }
}
