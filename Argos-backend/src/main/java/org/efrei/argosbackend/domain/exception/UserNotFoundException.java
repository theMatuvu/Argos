package org.efrei.argosbackend.domain.exception;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(String username) {
        super(String.format("Utilisateur %s introuvable", username));
    }
}
