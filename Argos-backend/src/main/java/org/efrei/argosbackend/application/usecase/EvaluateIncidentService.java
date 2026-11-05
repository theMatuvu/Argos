package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.port.in.EvaluateIncidentUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EvaluateIncidentService implements EvaluateIncidentUseCase {

    private final IncidentRepositoryPort incidentRepository;
    private final ProviderSurveillanceCalculator providerSurveillanceCalculator;
    private final Clock clock;

    public EvaluateIncidentService(IncidentRepositoryPort incidentRepository, ProviderSurveillanceCalculator providerSurveillanceCalculator,
                                   Clock clock) {
        this.incidentRepository = incidentRepository;
        this.providerSurveillanceCalculator = providerSurveillanceCalculator;
        this.clock = clock;
    }

    @Override
    public IncidentDetails evaluate(UUID incidentId, int rating) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        LocalDateTime now = LocalDateTime.now(clock);
        Incident evaluated = incidentRepository.save(incident.evaluate(new Rating(rating), now));
        return IncidentDetails.of(evaluated, now, providerSurveillanceCalculator.of(evaluated.provider()));
    }
}
