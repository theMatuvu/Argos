package org.efrei.argosbackend.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HashedPasswordTest {

    @Test
    void neverDisplaysTheHashedValue() {
        HashedPassword password = new HashedPassword("$2a$10$secret-hash");

        assertEquals("*****", password.toString());
        assertFalse(new User(null, "Jean", "Dupont", "jdupont", null, password).toString().contains("secret-hash"));
    }
}
