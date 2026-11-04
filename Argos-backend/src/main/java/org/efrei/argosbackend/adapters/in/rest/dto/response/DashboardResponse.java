package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.List;

public record DashboardResponse(
        DashboardIndicatorsResponse indicators,
        List<IncidentDetailsResponse> activeIncidents,
        List<IncidentDetailsResponse> incidentsAwaitingEvaluation
) {
}
