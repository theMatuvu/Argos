package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;

import java.util.UUID;

public record Equipment(
        UUID id,
        String reference,
        String label,
        EquipmentType type,
        Station station,
        OperatingStatus operatingStatus
) {
}
