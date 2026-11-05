package org.efrei.argosbackend.adapters.out.persistance.projection;

import org.efrei.argosbackend.domain.model.enums.EquipmentType;

public interface EquipmentTypeIncidentCountView {
    EquipmentType getEquipmentType();

    long getIncidentCount();
}
