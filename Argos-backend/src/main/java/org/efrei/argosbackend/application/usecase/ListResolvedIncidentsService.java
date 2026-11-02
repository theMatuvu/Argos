package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.port.in.ListResolvedIncidentsUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ListResolvedIncidentsService implements ListResolvedIncidentsUseCase {

    private static final Comparator<Incident> MOST_RECENTLY_RESOLVED_FIRST =
            Comparator.comparing(Incident::resolvedAt).reversed();

    private final IncidentRepositoryPort incidentRepository;
    private final Clock clock;

    public ListResolvedIncidentsService(IncidentRepositoryPort incidentRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    @Override
    public List<IncidentDetails> list() {
        LocalDateTime now = LocalDateTime.now(clock);
        return incidentRepository.findAllResolved().stream()
                .sorted(MOST_RECENTLY_RESOLVED_FIRST)
                .map(incident -> IncidentDetails.of(incident, now))
                .toList();
    }
}
