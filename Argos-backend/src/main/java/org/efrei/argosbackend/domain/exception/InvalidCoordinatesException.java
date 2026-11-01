package org.efrei.argosbackend.domain.exception;

public class InvalidCoordinatesException extends RuntimeException {

    public InvalidCoordinatesException(String stationName, double latitude, double longitude) {
        super(String.format("Coordonnées invalides pour la station %s : latitude %s, longitude %s.", stationName, latitude, longitude));
    }
}
