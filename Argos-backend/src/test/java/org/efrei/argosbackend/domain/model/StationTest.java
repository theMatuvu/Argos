package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.InvalidCoordinatesException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StationTest {

    @ParameterizedTest(name = "latitude {0}, longitude {1}")
    @CsvSource({"90.1, 2.35", "-90.1, 2.35", "48.85, 180.1", "48.85, -180.1"})
    void refusesCoordinatesOutOfRange(double latitude, double longitude) {
        assertThrows(InvalidCoordinatesException.class, () -> new Station(UUID.randomUUID(), "Station", latitude, longitude));
    }

    @Test
    void acceptsCoordinatesOnTheBounds() {
        assertDoesNotThrow(() -> new Station(UUID.randomUUID(), "Station", 90, -180));
    }
}
