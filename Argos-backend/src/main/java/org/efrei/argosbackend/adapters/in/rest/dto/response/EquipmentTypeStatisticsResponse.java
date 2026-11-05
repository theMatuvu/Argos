package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.EquipmentType;

public record EquipmentTypeStatisticsResponse(EquipmentType type, long incidentCount, Long averageResolutionMinutes) {
}
