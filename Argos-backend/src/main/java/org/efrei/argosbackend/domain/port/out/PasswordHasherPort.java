package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.HashedPassword;

public interface PasswordHasherPort {
    HashedPassword hash(String rawPassword);
}
