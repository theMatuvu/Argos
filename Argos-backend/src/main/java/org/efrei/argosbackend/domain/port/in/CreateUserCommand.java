package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.enums.ProfileType;

public record CreateUserCommand(String firstName, String lastName, String username, String password, ProfileType profile) {
}
