package org.efrei.argosbackend.adapters.in.rest.dto.response;

public record StatisticsIndicatorsResponse(
        long activeIncidentCount,
        long unassignedIncidentCount,
        long openedIncidentCount,
        long resolvedIncidentCount,
        Long averageResolutionMinutes,
        Double averageRating,
        int evaluationCount
) {
}
