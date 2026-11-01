package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class EquipmentNotFoundException extends RuntimeException {

    public EquipmentNotFoundException(UUID equipmentId) {
        super(String.format("L'équipement %s est introuvable.", equipmentId));
    }
}
