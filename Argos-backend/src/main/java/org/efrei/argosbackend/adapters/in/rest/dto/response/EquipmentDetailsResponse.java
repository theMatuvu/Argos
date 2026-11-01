package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;

import java.util.List;
import java.util.UUID;

public record EquipmentDetailsResponse(
        UUID id,
        String reference,
        String label,
        EquipmentType type,
        OperatingStatus operatingStatus,
        UUID stationId,
        String stationName,
        List<IncidentResponse> activeIncidents,
        List<IncidentResponse> pastIncidents
) {
}
