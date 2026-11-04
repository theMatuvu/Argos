package org.efrei.argosbackend.domain.exception;

import org.efrei.argosbackend.domain.model.Rating;

public class InvalidRatingException extends BusinessRuleException {

    public InvalidRatingException(int value) {
        super(String.format("La note %d est invalide : elle doit être un entier compris entre %d et %d.",
                value, Rating.MIN_VALUE, Rating.MAX_VALUE));
    }
}
