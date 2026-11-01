package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException(UUID stationId) {
        super(String.format("La station %s est introuvable.", stationId));
    }
}
