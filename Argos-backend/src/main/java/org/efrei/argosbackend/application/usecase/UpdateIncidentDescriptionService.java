package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.port.in.UpdateIncidentDescriptionUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UpdateIncidentDescriptionService implements UpdateIncidentDescriptionUseCase {

    private final IncidentRepositoryPort incidentRepository;
    private final Clock clock;

    public UpdateIncidentDescriptionService(IncidentRepositoryPort incidentRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    @Override
    public IncidentDetails update(UUID incidentId, String description) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        Incident updated = incidentRepository.save(incident.withDescription(description));
        return IncidentDetails.of(updated, LocalDateTime.now(clock));
    }
}
