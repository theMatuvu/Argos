package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.util.List;

public record StationEquipmentsResponse(StationResponse station, List<EquipmentResponse> equipments) {
}
