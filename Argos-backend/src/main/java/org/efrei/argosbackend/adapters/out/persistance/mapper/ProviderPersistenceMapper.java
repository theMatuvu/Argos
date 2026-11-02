package org.efrei.argosbackend.adapters.out.persistance.mapper;

import org.efrei.argosbackend.adapters.out.persistance.entity.ProviderEntity;
import org.efrei.argosbackend.domain.model.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderPersistenceMapper {

    public Provider toDomain(ProviderEntity provider) {
        return new Provider(provider.getId(), provider.getName(), provider.getContact());
    }
}
