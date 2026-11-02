package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.UUID;

public record StationResponse(UUID id, String name, double latitude, double longitude) {
}
