package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class IncidentAlreadyResolvedException extends BusinessRuleException {

    public IncidentAlreadyResolvedException(UUID incidentId) {
        super(String.format("L'incident %s est déjà résolu : il ne peut plus être modifié, affecté ni résolu.", incidentId));
    }
}
