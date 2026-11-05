package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.AttentionLevel;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

public record NetworkStatistics(
        Map<AttentionLevel, Long> activeIncidentsByAttention,
        List<DailyIncidentActivity> dailyActivity,
        List<EquipmentTypeStatistics> equipmentTypes,
        List<StationIncidentCount> mostAffectedStations,
        OptionalDouble averageResolutionMinutes,
        ProviderRating providerRating
) {

    public NetworkStatistics {
        activeIncidentsByAttention = Map.copyOf(activeIncidentsByAttention);
        dailyActivity = List.copyOf(dailyActivity);
        equipmentTypes = List.copyOf(equipmentTypes);
        mostAffectedStations = List.copyOf(mostAffectedStations);
    }

    public long activeIncidentCount() {
        return activeIncidentsByAttention.values().stream().mapToLong(Long::longValue).sum();
    }

    public long unassignedIncidentCount() {
        return activeIncidentCount() - activeIncidentsByAttention.getOrDefault(AttentionLevel.ASSIGNED, 0L);
    }

    public long openedIncidentCount() {
        return dailyActivity.stream().mapToLong(DailyIncidentActivity::openedCount).sum();
    }

    public long resolvedIncidentCount() {
        return dailyActivity.stream().mapToLong(DailyIncidentActivity::resolvedCount).sum();
    }
}
