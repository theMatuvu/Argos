package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.Provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderRepositoryPort {
    List<Provider> findAll();

    Optional<Provider> findById(UUID providerId);
}
