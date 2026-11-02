package org.efrei.argosbackend.domain.exception;

import java.util.UUID;

public class EquipmentNotFoundException extends ResourceNotFoundException {

    public EquipmentNotFoundException(UUID equipmentId) {
        super(String.format("L'équipement %s est introuvable.", equipmentId));
    }
}
