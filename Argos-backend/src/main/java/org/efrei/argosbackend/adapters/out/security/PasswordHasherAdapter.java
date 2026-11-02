package org.efrei.argosbackend.adapters.out.security;

import org.efrei.argosbackend.domain.model.HashedPassword;
import org.efrei.argosbackend.domain.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasherAdapter implements PasswordHasherPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasherAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public HashedPassword hash(String rawPassword) {
        return new HashedPassword(passwordEncoder.encode(rawPassword));
    }
}
