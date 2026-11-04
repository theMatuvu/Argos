package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.ProviderDetails;

import java.util.UUID;

public interface FindProviderDetailsUseCase {
    ProviderDetails find(UUID providerId);
}
