package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.ProfileType;

import java.util.UUID;

public record UserResponse(UUID id, String username, String firstName, String lastName, ProfileType profile) {
}
