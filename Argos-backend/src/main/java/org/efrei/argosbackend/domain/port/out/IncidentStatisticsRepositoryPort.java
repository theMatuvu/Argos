package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.StationIncidentCount;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;

import java.util.List;
import java.util.Map;

public interface IncidentStatisticsRepositoryPort {
    Map<EquipmentType, Long> countIncidentsByEquipmentType();

    List<StationIncidentCount> findMostAffectedStations(int limit);
}
