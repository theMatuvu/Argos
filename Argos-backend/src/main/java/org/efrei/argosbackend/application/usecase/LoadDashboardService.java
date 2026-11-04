package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Dashboard;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.in.LoadDashboardUseCase;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class LoadDashboardService implements LoadDashboardUseCase {

    private static final Comparator<Incident> OLDEST_OPENED_FIRST = Comparator.comparing(Incident::openedAt);
    private static final Comparator<Incident> MOST_RECENTLY_RESOLVED_FIRST =
            Comparator.comparing(Incident::resolvedAt).reversed();

    private final IncidentRepositoryPort incidentRepository;
    private final EquipmentRepositoryPort equipmentRepository;
    private final Clock clock;

    public LoadDashboardService(IncidentRepositoryPort incidentRepository, EquipmentRepositoryPort equipmentRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.equipmentRepository = equipmentRepository;
        this.clock = clock;
    }

    @Override
    public Dashboard load() {
        LocalDateTime now = LocalDateTime.now(clock);
        return new Dashboard(
                toDetails(incidentRepository.findAllActive(), OLDEST_OPENED_FIRST, now),
                toDetails(awaitingEvaluation(), MOST_RECENTLY_RESOLVED_FIRST, now),
                equipmentRepository.countByOperatingStatus(OperatingStatus.OUT_OF_SERVICE)
        );
    }

    private List<Incident> awaitingEvaluation() {
        return incidentRepository.findAllResolved().stream()
                .filter(Incident::isAwaitingEvaluation)
                .toList();
    }

    private List<IncidentDetails> toDetails(List<Incident> incidents, Comparator<Incident> order, LocalDateTime now) {
        return incidents.stream()
                .sorted(order)
                .map(incident -> IncidentDetails.of(incident, now))
                .toList();
    }
}
