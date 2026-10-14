package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.User;

import java.util.Optional;

public interface FindUserByUsernameUseCase {
    User findByUsername(String username);
}
