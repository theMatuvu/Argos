package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.ProfileType;

import java.util.UUID;

public record User(UUID id, String firstName, String lastName, String username, ProfileType profile, HashedPassword hashedPassword) {



}
