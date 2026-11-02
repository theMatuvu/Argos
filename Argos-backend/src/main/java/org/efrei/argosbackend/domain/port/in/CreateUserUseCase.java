package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.User;

public interface CreateUserUseCase {
    User create(CreateUserCommand command);
}
