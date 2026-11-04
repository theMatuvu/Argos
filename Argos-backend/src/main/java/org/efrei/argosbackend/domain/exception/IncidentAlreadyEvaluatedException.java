package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class IncidentAlreadyEvaluatedException extends BusinessRuleException {

    public IncidentAlreadyEvaluatedException(UUID incidentId) {
        super(String.format("L'intervention sur l'incident %s a déjà été évaluée.", incidentId));
    }
}
