package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class InvalidResolutionDateException extends RuntimeException {

    public InvalidResolutionDateException(UUID incidentId) {
        super(String.format("La date de résolution de l'incident %s précède sa date d'ouverture.", incidentId));
    }
}
