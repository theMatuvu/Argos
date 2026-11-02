package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.mapper.ProviderPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.ProviderJpaRepository;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProviderPersistenceAdapter implements ProviderRepositoryPort {

    private final ProviderJpaRepository providerJpaRepository;
    private final ProviderPersistenceMapper providerPersistenceMapper;

    public ProviderPersistenceAdapter(ProviderJpaRepository providerJpaRepository, ProviderPersistenceMapper providerPersistenceMapper) {
        this.providerJpaRepository = providerJpaRepository;
        this.providerPersistenceMapper = providerPersistenceMapper;
    }

    @Override
    public List<Provider> findAll() {
        return providerJpaRepository.findAllByOrderByNameAsc().stream()
                .map(providerPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Provider> findById(UUID providerId) {
        return providerJpaRepository.findById(providerId).map(providerPersistenceMapper::toDomain);
    }
}
