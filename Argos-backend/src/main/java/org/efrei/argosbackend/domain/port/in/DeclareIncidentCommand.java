package org.efrei.argosbackend.domain.port.in;

import java.util.UUID;

public record DeclareIncidentCommand(UUID equipmentId, String description) {
}
