package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.request.IncidentDescriptionRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentResponse;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.port.in.DeclareIncidentCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IncidentRestMapper {

    private final ProviderRestMapper providerRestMapper;

    public IncidentRestMapper(ProviderRestMapper providerRestMapper) {
        this.providerRestMapper = providerRestMapper;
    }

    public IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.id(),
                incident.description(),
                incident.openedAt(),
                incident.status(),
                providerRestMapper.toResponse(incident.provider()),
                incident.resolvedAt()
        );
    }

    public IncidentDetailsResponse toResponse(IncidentDetails details) {
        Incident incident = details.incident();
        Equipment equipment = incident.equipment();
        return new IncidentDetailsResponse(
                incident.id(),
                incident.description(),
                incident.status(),
                incident.openedAt(),
                incident.resolvedAt(),
                details.openDuration().toMinutes(),
                providerRestMapper.toResponse(incident.provider()),
                equipment.id(),
                equipment.reference(),
                equipment.label(),
                equipment.type(),
                equipment.station().id(),
                equipment.station().name()
        );
    }

    public DeclareIncidentCommand toCommand(UUID equipmentId, IncidentDescriptionRequest request) {
        return new DeclareIncidentCommand(equipmentId, request.description());
    }
}
