package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.InvalidCoordinatesException;

import java.util.UUID;

public record Station(
        UUID id,
        String name,
        double latitude,
        double longitude
) {

    private static final double MAX_ABSOLUTE_LATITUDE = 90;
    private static final double MAX_ABSOLUTE_LONGITUDE = 180;

    public Station {
        if (Math.abs(latitude) > MAX_ABSOLUTE_LATITUDE || Math.abs(longitude) > MAX_ABSOLUTE_LONGITUDE) {
            throw new InvalidCoordinatesException(name, latitude, longitude);
        }
    }
}
