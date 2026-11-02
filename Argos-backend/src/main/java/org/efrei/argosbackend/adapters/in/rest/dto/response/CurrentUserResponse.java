package org.efrei.argosbackend.adapters.in.rest.dto.response;

import org.efrei.argosbackend.domain.model.enums.ProfileType;

public record CurrentUserResponse(String firstName, ProfileType profile) {
}
