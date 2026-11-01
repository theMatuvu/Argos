package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderResponse;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.springframework.stereotype.Component;

@Component
public class IncidentRestMapper {

    public IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.id(),
                incident.description(),
                incident.openedAt(),
                incident.status(),
                toResponse(incident.provider()),
                incident.resolvedAt()
        );
    }

    private ProviderResponse toResponse(Provider provider) {
        return provider == null ? null : new ProviderResponse(provider.id(), provider.name(), provider.contact());
    }
}
