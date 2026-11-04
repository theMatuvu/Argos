package org.efrei.argosbackend.adapters.in.rest.dto.response;

public record DashboardIndicatorsResponse(
        long unassignedCount,
        long assignedCount,
        long awaitingEvaluationCount,
        long outOfServiceEquipmentCount
) {
}
