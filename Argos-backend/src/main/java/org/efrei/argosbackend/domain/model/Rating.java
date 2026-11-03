package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.InvalidRatingException;

public record Rating(int value) {

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 5;

    public Rating {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new InvalidRatingException(value);
        }
    }
}
