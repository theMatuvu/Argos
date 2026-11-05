package org.efrei.argosbackend.domain.model;

import java.util.List;
import java.util.Objects;

public record ProviderDetails(Provider provider, List<IncidentDetails> resolvedIncidents) {

    public ProviderDetails {
        resolvedIncidents = List.copyOf(resolvedIncidents);
    }

    public ProviderRating rating() {
        return ProviderRating.of(resolvedIncidents.stream()
                .map(details -> details.incident().rating())
                .filter(Objects::nonNull)
                .toList());
    }

    public ProviderSurveillance surveillance() {
        return ProviderSurveillance.of(resolvedIncidents.stream().map(IncidentDetails::incident).toList());
    }
}
