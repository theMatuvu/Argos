package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.List;
import java.util.UUID;

public record ProviderDetailsResponse(
        UUID id,
        String name,
        String contact,
        Double averageRating,
        int evaluationCount,
        List<IncidentDetailsResponse> resolvedIncidents
) {
}
