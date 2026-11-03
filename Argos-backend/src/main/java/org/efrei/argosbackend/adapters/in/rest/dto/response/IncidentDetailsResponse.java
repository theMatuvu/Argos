package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.model.enums.IncidentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentDetailsResponse(
        UUID id,
        String description,
        IncidentStatus status,
        LocalDateTime openedAt,
        LocalDateTime resolvedAt,
        long openDurationMinutes,
        Integer rating,
        ProviderResponse provider,
        UUID equipmentId,
        String equipmentReference,
        String equipmentLabel,
        EquipmentType equipmentType,
        UUID stationId,
        String stationName
) {
}
