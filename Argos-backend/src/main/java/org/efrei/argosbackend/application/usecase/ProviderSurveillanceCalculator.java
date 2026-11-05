package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProviderSurveillanceCalculator {

    private final IncidentRepositoryPort incidentRepository;

    public ProviderSurveillanceCalculator(IncidentRepositoryPort incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public ProviderSurveillance of(Provider provider) {
        if (provider == null) {
            return ProviderSurveillance.none();
        }
        return ProviderSurveillance.of(incidentRepository.findAllResolvedByProviderId(provider.id()));
    }

    public Function<Provider, ProviderSurveillance> forAllProviders() {
        Map<UUID, ProviderSurveillance> surveillanceByProvider = incidentRepository.findAllResolved().stream()
                .filter(Incident::isEvaluated)
                .collect(Collectors.groupingBy(
                        incident -> incident.provider().id(),
                        Collectors.collectingAndThen(Collectors.toList(), ProviderSurveillance::of)
                ));
        return provider -> provider == null
                ? ProviderSurveillance.none()
                : surveillanceByProvider.getOrDefault(provider.id(), ProviderSurveillance.none());
    }
}
