package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.exception.ProviderNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.port.in.AssignProviderUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AssignProviderService implements AssignProviderUseCase {

    private final IncidentRepositoryPort incidentRepository;
    private final ProviderRepositoryPort providerRepository;
    private final Clock clock;

    public AssignProviderService(IncidentRepositoryPort incidentRepository, ProviderRepositoryPort providerRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.providerRepository = providerRepository;
        this.clock = clock;
    }

    @Override
    public IncidentDetails assign(UUID incidentId, UUID providerId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException(providerId));
        Incident assigned = incidentRepository.save(incident.assignTo(provider));
        return IncidentDetails.of(assigned, LocalDateTime.now(clock));
    }
}
