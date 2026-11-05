package org.efrei.argosbackend.domain.port.in;

import java.util.UUID;

public record AssignProviderCommand(UUID incidentId, UUID providerId, boolean surveillanceConfirmed) {
}
