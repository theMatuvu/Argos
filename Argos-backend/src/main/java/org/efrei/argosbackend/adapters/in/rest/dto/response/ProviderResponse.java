package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.UUID;

public record ProviderResponse(UUID id, String name, String contact) {
}
