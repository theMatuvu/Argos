package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class IncidentNotFoundException extends ResourceNotFoundException {

    public IncidentNotFoundException(UUID incidentId) {
        super(String.format("L'incident %s est introuvable.", incidentId));
    }
}
