package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.List;
import java.util.UUID;

public record LineResponse(UUID id, String name, String color, String textColor, List<LineSegmentResponse> segments) {
}
