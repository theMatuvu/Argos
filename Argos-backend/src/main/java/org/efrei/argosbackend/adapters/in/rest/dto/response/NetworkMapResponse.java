package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.List;

public record NetworkMapResponse(List<StationResponse> stations, List<LineResponse> lines) {
}
