package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.DailyIncidentActivity;
import org.efrei.argosbackend.domain.model.EquipmentTypeStatistics;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.NetworkStatistics;
import org.efrei.argosbackend.domain.model.ProviderRating;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.port.in.LoadStatisticsUseCase;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentStatisticsRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoadStatisticsService implements LoadStatisticsUseCase {

    private static final int ACTIVITY_PERIOD_DAYS = 30;
    private static final int MOST_AFFECTED_STATIONS_LIMIT = 8;

    private final IncidentRepositoryPort incidentRepository;
    private final IncidentStatisticsRepositoryPort incidentStatisticsRepository;
    private final Clock clock;

    public LoadStatisticsService(IncidentRepositoryPort incidentRepository,
                                 IncidentStatisticsRepositoryPort incidentStatisticsRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.incidentStatisticsRepository = incidentStatisticsRepository;
        this.clock = clock;
    }

    @Override
    public NetworkStatistics load() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Incident> activeIncidents = incidentRepository.findAllActive();
        List<Incident> resolvedIncidents = incidentRepository.findAllResolved();
        return new NetworkStatistics(
                countByAttentionLevel(activeIncidents, now),
                dailyActivity(Stream.concat(activeIncidents.stream(), resolvedIncidents.stream()).toList(), now.toLocalDate()),
                equipmentTypeStatistics(resolvedIncidents),
                incidentStatisticsRepository.findMostAffectedStations(MOST_AFFECTED_STATIONS_LIMIT),
                averageResolutionMinutes(resolvedIncidents),
                ProviderRating.of(ratings(resolvedIncidents))
        );
    }

    private Map<AttentionLevel, Long> countByAttentionLevel(List<Incident> activeIncidents, LocalDateTime now) {
        Map<AttentionLevel, Long> counts = new EnumMap<>(AttentionLevel.class);
        Arrays.stream(AttentionLevel.values()).forEach(level -> counts.put(level, 0L));
        activeIncidents.forEach(incident -> counts.merge(incident.attentionLevel(now), 1L, Long::sum));
        return counts;
    }

    private List<DailyIncidentActivity> dailyActivity(List<Incident> incidents, LocalDate today) {
        Map<LocalDate, Long> openedByDay = countByDay(incidents.stream().map(Incident::openedAt));
        Map<LocalDate, Long> resolvedByDay = countByDay(incidents.stream()
                .filter(Incident::isResolved)
                .map(Incident::resolvedAt));
        return today.minusDays(ACTIVITY_PERIOD_DAYS - 1L).datesUntil(today.plusDays(1))
                .map(day -> new DailyIncidentActivity(day, openedByDay.getOrDefault(day, 0L), resolvedByDay.getOrDefault(day, 0L)))
                .toList();
    }

    private Map<LocalDate, Long> countByDay(Stream<LocalDateTime> moments) {
        return moments.collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));
    }

    private List<EquipmentTypeStatistics> equipmentTypeStatistics(List<Incident> resolvedIncidents) {
        Map<EquipmentType, Long> incidentCounts = incidentStatisticsRepository.countIncidentsByEquipmentType();
        Map<EquipmentType, List<Incident>> resolvedByType = resolvedIncidents.stream()
                .collect(Collectors.groupingBy(incident -> incident.equipment().type()));
        return Arrays.stream(EquipmentType.values())
                .map(type -> new EquipmentTypeStatistics(
                        type,
                        incidentCounts.getOrDefault(type, 0L),
                        averageResolutionMinutes(resolvedByType.getOrDefault(type, List.of()))
                ))
                .toList();
    }

    private OptionalDouble averageResolutionMinutes(List<Incident> resolvedIncidents) {
        return resolvedIncidents.stream()
                .map(Incident::resolutionTime)
                .flatMap(Optional::stream)
                .mapToLong(Duration::toMinutes)
                .average();
    }

    private List<Rating> ratings(List<Incident> resolvedIncidents) {
        return resolvedIncidents.stream()
                .filter(Incident::isEvaluated)
                .map(Incident::rating)
                .toList();
    }
}
