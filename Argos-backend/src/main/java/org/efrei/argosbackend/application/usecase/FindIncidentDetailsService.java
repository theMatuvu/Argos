package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.port.in.FindIncidentDetailsUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FindIncidentDetailsService implements FindIncidentDetailsUseCase {

    private final IncidentRepositoryPort incidentRepository;
    private final ProviderSurveillanceCalculator providerSurveillanceCalculator;
    private final Clock clock;

    public FindIncidentDetailsService(IncidentRepositoryPort incidentRepository, ProviderSurveillanceCalculator providerSurveillanceCalculator,
                                      Clock clock) {
        this.incidentRepository = incidentRepository;
        this.providerSurveillanceCalculator = providerSurveillanceCalculator;
        this.clock = clock;
    }

    @Override
    public IncidentDetails find(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        return IncidentDetails.of(incident, LocalDateTime.now(clock), providerSurveillanceCalculator.of(incident.provider()));
    }
}
