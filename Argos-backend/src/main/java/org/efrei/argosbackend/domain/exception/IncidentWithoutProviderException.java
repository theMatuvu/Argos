package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class IncidentWithoutProviderException extends BusinessRuleException {

    public IncidentWithoutProviderException(UUID incidentId) {
        super(String.format("L'incident %s n'a été affecté à aucun prestataire : il n'y a pas d'intervention à évaluer.", incidentId));
    }
}
