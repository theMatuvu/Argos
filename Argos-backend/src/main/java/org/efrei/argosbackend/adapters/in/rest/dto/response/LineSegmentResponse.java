package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.UUID;

public record LineSegmentResponse(UUID fromStationId, UUID toStationId) {
}
