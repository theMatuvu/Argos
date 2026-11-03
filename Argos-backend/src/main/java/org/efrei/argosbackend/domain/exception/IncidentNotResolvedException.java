package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class IncidentNotResolvedException extends BusinessRuleException {

    public IncidentNotResolvedException(UUID incidentId) {
        super(String.format("L'incident %s n'est pas encore résolu : l'intervention ne peut pas être évaluée.", incidentId));
    }
}
