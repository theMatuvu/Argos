package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.EquipmentDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.EquipmentResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentResponse;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.EquipmentDetails;
import org.efrei.argosbackend.domain.model.Incident;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipmentRestMapper {

    private final IncidentRestMapper incidentRestMapper;

    public EquipmentRestMapper(IncidentRestMapper incidentRestMapper) {
        this.incidentRestMapper = incidentRestMapper;
    }

    public EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.id(),
                equipment.reference(),
                equipment.label(),
                equipment.type(),
                equipment.operatingStatus()
        );
    }

    public EquipmentDetailsResponse toResponse(EquipmentDetails details) {
        Equipment equipment = details.equipment();
        return new EquipmentDetailsResponse(
                equipment.id(),
                equipment.reference(),
                equipment.label(),
                equipment.type(),
                equipment.operatingStatus(),
                equipment.station().id(),
                equipment.station().name(),
                toResponses(details.activeIncidents()),
                toResponses(details.pastIncidents())
        );
    }

    private List<IncidentResponse> toResponses(List<Incident> incidents) {
        return incidents.stream().map(incidentRestMapper::toResponse).toList();
    }
}
