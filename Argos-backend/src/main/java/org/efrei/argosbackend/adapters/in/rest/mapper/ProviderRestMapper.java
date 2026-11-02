package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderResponse;
import org.efrei.argosbackend.domain.model.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderRestMapper {

    public ProviderResponse toResponse(Provider provider) {
        return provider == null ? null : new ProviderResponse(provider.id(), provider.name(), provider.contact());
    }
}
