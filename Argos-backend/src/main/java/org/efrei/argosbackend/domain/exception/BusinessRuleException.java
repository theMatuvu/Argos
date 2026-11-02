package org.efrei.argosbackend.domain.exception;

public abstract class BusinessRuleException extends RuntimeException {

    protected BusinessRuleException(String message) {
        super(message);
    }
}
