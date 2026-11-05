package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.projection.EquipmentTypeIncidentCountView;
import org.efrei.argosbackend.adapters.out.persistance.repository.IncidentJpaRepository;
import org.efrei.argosbackend.domain.model.StationIncidentCount;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.port.out.IncidentStatisticsRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IncidentStatisticsPersistenceAdapter implements IncidentStatisticsRepositoryPort {

    private final IncidentJpaRepository incidentJpaRepository;

    public IncidentStatisticsPersistenceAdapter(IncidentJpaRepository incidentJpaRepository) {
        this.incidentJpaRepository = incidentJpaRepository;
    }

    @Override
    public Map<EquipmentType, Long> countIncidentsByEquipmentType() {
        return incidentJpaRepository.countGroupedByEquipmentType().stream()
                .collect(Collectors.toMap(
                        EquipmentTypeIncidentCountView::getEquipmentType,
                        EquipmentTypeIncidentCountView::getIncidentCount
                ));
    }

    @Override
    public List<StationIncidentCount> findMostAffectedStations(int limit) {
        return incidentJpaRepository.findMostAffectedStations(limit).stream()
                .map(station -> new StationIncidentCount(station.getStationName(), station.getIncidentCount()))
                .toList();
    }
}
