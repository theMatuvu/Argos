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
    private final Clock clock;

    public EvaluateIncidentService(IncidentRepositoryPort incidentRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    @Override
    public IncidentDetails evaluate(UUID incidentId, int rating) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        Incident evaluated = incidentRepository.save(incident.evaluate(new Rating(rating)));
        return IncidentDetails.of(evaluated, LocalDateTime.now(clock));
    }
}
