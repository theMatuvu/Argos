package org.efrei.argosbackend.domain.model;

import java.util.List;

public record StationEquipments(Station station, List<Equipment> equipments) {

    public StationEquipments {
        equipments = List.copyOf(equipments);
    }
}
