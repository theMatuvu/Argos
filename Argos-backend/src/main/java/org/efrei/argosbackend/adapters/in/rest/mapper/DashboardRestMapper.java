package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.DashboardIndicatorsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.DashboardResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentDetailsResponse;
import org.efrei.argosbackend.domain.model.Dashboard;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardRestMapper {

    private final IncidentRestMapper incidentRestMapper;

    public DashboardRestMapper(IncidentRestMapper incidentRestMapper) {
        this.incidentRestMapper = incidentRestMapper;
    }

    public DashboardResponse toResponse(Dashboard dashboard) {
        return new DashboardResponse(
                toIndicators(dashboard),
                toResponses(dashboard.activeIncidents()),
                toResponses(dashboard.incidentsAwaitingEvaluation())
        );
    }

    private DashboardIndicatorsResponse toIndicators(Dashboard dashboard) {
        return new DashboardIndicatorsResponse(
                dashboard.unassignedCount(),
                dashboard.assignedCount(),
                dashboard.incidentsAwaitingEvaluation().size(),
                dashboard.outOfServiceEquipmentCount()
        );
    }

    private List<IncidentDetailsResponse> toResponses(List<IncidentDetails> incidents) {
        return incidents.stream().map(incidentRestMapper::toResponse).toList();
    }
}
