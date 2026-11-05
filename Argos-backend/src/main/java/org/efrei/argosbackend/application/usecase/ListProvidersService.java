package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderRating;
import org.efrei.argosbackend.domain.model.ProviderSummary;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
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
        Map<UUID, List<Incident>> evaluatedIncidentsByProvider = evaluatedIncidentsByProvider();
        return providerRepository.findAll().stream()
                .map(provider -> toSummary(provider, evaluatedIncidentsByProvider.getOrDefault(provider.id(), List.of())))
                .toList();
    }

    private Map<UUID, List<Incident>> evaluatedIncidentsByProvider() {
        return incidentRepository.findAllResolved().stream()
                .filter(Incident::isEvaluated)
                .collect(Collectors.groupingBy(incident -> incident.provider().id()));
    }

    private ProviderSummary toSummary(Provider provider, List<Incident> evaluatedIncidents) {
        ProviderRating rating = ProviderRating.of(evaluatedIncidents.stream().map(Incident::rating).toList());
        return new ProviderSummary(provider, rating, ProviderSurveillance.of(evaluatedIncidents));
    }
}
