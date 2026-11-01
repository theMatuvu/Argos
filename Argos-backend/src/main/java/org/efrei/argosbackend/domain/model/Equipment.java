package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.enums.EquipmentType;

import java.util.UUID;

public record Equipment(
        UUID id,
        String name,
        String reference,
        String label,
        EquipmentType equipmentType,
        Station station,
        boolean isBroken
) {
}
