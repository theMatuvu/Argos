package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    User save(User user);
}
