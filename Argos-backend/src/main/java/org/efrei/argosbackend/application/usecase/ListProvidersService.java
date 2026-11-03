package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderRating;
import org.efrei.argosbackend.domain.model.ProviderSummary;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.port.in.ListProvidersUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListProvidersService implements ListProvidersUseCase {

    private final ProviderRepositoryPort providerRepository;
    private final IncidentRepositoryPort incidentRepository;

    public ListProvidersService(ProviderRepositoryPort providerRepository, IncidentRepositoryPort incidentRepository) {
        this.providerRepository = providerRepository;
        this.incidentRepository = incidentRepository;
    }

    @Override
    public List<ProviderSummary> list() {
        Map<UUID, List<Rating>> ratingsByProvider = ratingsByProvider();
        return providerRepository.findAll().stream()
                .map(provider -> toSummary(provider, ratingsByProvider))
                .toList();
    }

    private Map<UUID, List<Rating>> ratingsByProvider() {
        return incidentRepository.findAllResolved().stream()
                .filter(Incident::isEvaluated)
                .collect(Collectors.groupingBy(
                        incident -> incident.provider().id(),
                        Collectors.mapping(Incident::rating, Collectors.toList())
                ));
    }

    private ProviderSummary toSummary(Provider provider, Map<UUID, List<Rating>> ratingsByProvider) {
        return new ProviderSummary(provider, ProviderRating.of(ratingsByProvider.getOrDefault(provider.id(), List.of())));
    }
}
