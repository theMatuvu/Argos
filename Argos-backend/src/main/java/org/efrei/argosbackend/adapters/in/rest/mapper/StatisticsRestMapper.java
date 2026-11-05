package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.DailyIncidentActivityResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.EquipmentTypeStatisticsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.StationIncidentCountResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.StatisticsIndicatorsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.StatisticsResponse;
import org.efrei.argosbackend.domain.model.DailyIncidentActivity;
import org.efrei.argosbackend.domain.model.EquipmentTypeStatistics;
import org.efrei.argosbackend.domain.model.NetworkStatistics;
import org.efrei.argosbackend.domain.model.ProviderRating;
import org.efrei.argosbackend.domain.model.StationIncidentCount;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class StatisticsRestMapper {

    private final ProviderRestMapper providerRestMapper;

    public StatisticsRestMapper(ProviderRestMapper providerRestMapper) {
        this.providerRestMapper = providerRestMapper;
    }

    public StatisticsResponse toResponse(NetworkStatistics statistics) {
        return new StatisticsResponse(
                toIndicators(statistics),
                statistics.activeIncidentsByAttention(),
                statistics.dailyActivity().stream().map(this::toResponse).toList(),
                statistics.equipmentTypes().stream().map(this::toResponse).toList(),
                statistics.mostAffectedStations().stream().map(this::toResponse).toList()
        );
    }

    private StatisticsIndicatorsResponse toIndicators(NetworkStatistics statistics) {
        ProviderRating providerRating = statistics.providerRating();
        return new StatisticsIndicatorsResponse(
                statistics.activeIncidentCount(),
                statistics.unassignedIncidentCount(),
                statistics.openedIncidentCount(),
                statistics.resolvedIncidentCount(),
                roundedMinutes(statistics.averageResolutionMinutes()),
                providerRestMapper.averageRating(providerRating),
                providerRating.evaluationCount()
        );
    }

    private DailyIncidentActivityResponse toResponse(DailyIncidentActivity activity) {
        return new DailyIncidentActivityResponse(activity.day(), activity.openedCount(), activity.resolvedCount());
    }

    private EquipmentTypeStatisticsResponse toResponse(EquipmentTypeStatistics statistics) {
        return new EquipmentTypeStatisticsResponse(
                statistics.type(),
                statistics.incidentCount(),
                roundedMinutes(statistics.averageResolutionMinutes())
        );
    }

    private StationIncidentCountResponse toResponse(StationIncidentCount stationIncidentCount) {
        return new StationIncidentCountResponse(stationIncidentCount.stationName(), stationIncidentCount.incidentCount());
    }

    private Long roundedMinutes(OptionalDouble minutes) {
        return minutes.isPresent() ? Math.round(minutes.getAsDouble()) : null;
    }
}
