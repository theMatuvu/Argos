package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.port.in.ListProvidersUseCase;
import org.efrei.argosbackend.domain.port.out.ProviderRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProvidersService implements ListProvidersUseCase {

    private final ProviderRepositoryPort providerRepository;

    public ListProvidersService(ProviderRepositoryPort providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public List<Provider> list() {
        return providerRepository.findAll();
    }
}
