package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.UUID;

public record ProviderSummaryResponse(
        UUID id,
        String name,
        String contact,
        Double averageRating,
        int evaluationCount,
        Double recentAverageRating,
        boolean underSurveillance
) {
}
