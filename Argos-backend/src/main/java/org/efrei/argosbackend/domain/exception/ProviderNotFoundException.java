package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class ProviderNotFoundException extends ResourceNotFoundException {

    public ProviderNotFoundException(UUID providerId) {
        super(String.format("Le prestataire %s est introuvable.", providerId));
    }
}
