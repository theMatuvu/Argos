package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.EquipmentType;

import java.util.OptionalDouble;

public record EquipmentTypeStatistics(EquipmentType type, long incidentCount, OptionalDouble averageResolutionMinutes) {
}
