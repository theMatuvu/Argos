package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.AttentionLevel;

import java.util.List;
import java.util.Map;

public record StatisticsResponse(
        StatisticsIndicatorsResponse indicators,
        Map<AttentionLevel, Long> activeIncidentsByAttention,
        List<DailyIncidentActivityResponse> dailyActivity,
        List<EquipmentTypeStatisticsResponse> equipmentTypes,
        List<StationIncidentCountResponse> mostAffectedStations
) {
}
