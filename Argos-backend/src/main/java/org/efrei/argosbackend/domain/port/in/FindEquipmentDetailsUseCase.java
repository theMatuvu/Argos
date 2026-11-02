package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.EquipmentDetails;

import java.util.UUID;

public interface FindEquipmentDetailsUseCase {
    EquipmentDetails find(UUID equipmentId);
}
