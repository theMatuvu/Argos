package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderDetailsResponse;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.ProviderDetails;
import org.efrei.argosbackend.domain.model.ProviderRating;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
import org.springframework.stereotype.Component;

@Component
public class ProviderDetailsRestMapper {

    private final ProviderRestMapper providerRestMapper;
    private final IncidentRestMapper incidentRestMapper;

    public ProviderDetailsRestMapper(ProviderRestMapper providerRestMapper, IncidentRestMapper incidentRestMapper) {
        this.providerRestMapper = providerRestMapper;
        this.incidentRestMapper = incidentRestMapper;
    }

    public ProviderDetailsResponse toResponse(ProviderDetails details) {
        Provider provider = details.provider();
        ProviderRating rating = details.rating();
        ProviderSurveillance surveillance = details.surveillance();
        return new ProviderDetailsResponse(
                provider.id(),
                provider.name(),
                provider.contact(),
                providerRestMapper.averageRating(rating),
                rating.evaluationCount(),
                providerRestMapper.recentAverageRating(surveillance),
                surveillance.isUnderSurveillance(),
                details.resolvedIncidents().stream().map(incidentRestMapper::toResponse).toList()
        );
    }
}
