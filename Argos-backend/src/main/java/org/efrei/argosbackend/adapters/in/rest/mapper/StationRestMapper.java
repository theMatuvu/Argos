package org.efrei.argosbackend.adapters.in.rest.mapper;

import org.efrei.argosbackend.adapters.in.rest.dto.response.StationEquipmentsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.StationResponse;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.model.StationEquipments;
import org.springframework.stereotype.Component;

@Component
public class StationRestMapper {

    private final EquipmentRestMapper equipmentRestMapper;

    public StationRestMapper(EquipmentRestMapper equipmentRestMapper) {
        this.equipmentRestMapper = equipmentRestMapper;
    }

    public StationResponse toResponse(Station station) {
        return new StationResponse(station.id(), station.name(), station.latitude(), station.longitude());
    }

    public StationEquipmentsResponse toResponse(StationEquipments stationEquipments) {
        return new StationEquipmentsResponse(
                toResponse(stationEquipments.station()),
                stationEquipments.equipments().stream().map(equipmentRestMapper::toResponse).toList()
        );
    }
}
