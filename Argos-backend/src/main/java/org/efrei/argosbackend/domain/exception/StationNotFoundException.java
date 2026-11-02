package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class StationNotFoundException extends ResourceNotFoundException {

    public StationNotFoundException(UUID stationId) {
        super(String.format("La station %s est introuvable.", stationId));
    }
}
