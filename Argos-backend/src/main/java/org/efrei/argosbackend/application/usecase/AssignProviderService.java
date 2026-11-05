package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.exception.ProviderNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
import org.efrei.argosbackend.domain.port.in.AssignProviderCommand;
import org.efrei.argosbackend.domain.port.in.AssignProviderUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class AssignProviderService implements AssignProviderUseCase {

    private final IncidentRepositoryPort incidentRepository;
    private final ProviderRepositoryPort providerRepository;
    private final ProviderSurveillanceCalculator providerSurveillanceCalculator;
    private final Clock clock;

    public AssignProviderService(IncidentRepositoryPort incidentRepository, ProviderRepositoryPort providerRepository,
                                 ProviderSurveillanceCalculator providerSurveillanceCalculator, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.providerRepository = providerRepository;
        this.providerSurveillanceCalculator = providerSurveillanceCalculator;
        this.clock = clock;
    }

    @Override
    public IncidentDetails assign(AssignProviderCommand command) {
        Incident incident = incidentRepository.findById(command.incidentId())
                .orElseThrow(() -> new IncidentNotFoundException(command.incidentId()));
        Provider provider = providerRepository.findById(command.providerId())
                .orElseThrow(() -> new ProviderNotFoundException(command.providerId()));
        ProviderSurveillance surveillance = providerSurveillanceCalculator.of(provider);
        surveillance.ensureAssignable(provider, command.surveillanceConfirmed());
        Incident assigned = incidentRepository.save(incident.assignTo(provider));
        return IncidentDetails.of(assigned, LocalDateTime.now(clock), surveillance);
    }
}
