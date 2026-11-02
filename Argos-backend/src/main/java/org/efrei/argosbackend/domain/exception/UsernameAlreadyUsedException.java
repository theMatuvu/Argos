package org.efrei.argosbackend.domain.exception;

public class UsernameAlreadyUsedException extends BusinessRuleException {

    public UsernameAlreadyUsedException(String username) {
        super(String.format("L'identifiant %s est déjà utilisé.", username));
    }
}
