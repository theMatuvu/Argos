package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.InvalidRatingException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingTest {

    @ParameterizedTest
    @ValueSource(ints = {Rating.MIN_VALUE, 2, 3, 4, Rating.MAX_VALUE})
    void acceptsAValueBetweenTheBounds(int value) {
        assertEquals(value, new Rating(value).value());
    }

    @ParameterizedTest
    @ValueSource(ints = {Rating.MIN_VALUE - 1, Rating.MAX_VALUE + 1, -3, 10})
    void refusesAValueOutsideTheBounds(int value) {
        assertThrows(InvalidRatingException.class, () -> new Rating(value));
    }
}
