package org.efrei.argosbackend.domain.model;

import java.util.UUID;

public record Provider(
        UUID id,
        String name,
        String contact
) {
}
