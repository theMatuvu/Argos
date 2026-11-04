package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderSummaryResponse;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderRating;
import org.efrei.argosbackend.domain.model.ProviderSummary;
import org.springframework.stereotype.Component;

@Component
public class ProviderRestMapper {

    public ProviderResponse toResponse(Provider provider) {
        return provider == null ? null : new ProviderResponse(provider.id(), provider.name(), provider.contact());
    }

    public ProviderSummaryResponse toResponse(ProviderSummary summary) {
        Provider provider = summary.provider();
        return new ProviderSummaryResponse(
                provider.id(),
                provider.name(),
                provider.contact(),
                averageRating(summary.rating()),
                summary.rating().evaluationCount()
        );
    }

    public Double averageRating(ProviderRating rating) {
        return rating.average().isPresent() ? rating.average().getAsDouble() : null;
    }
}
