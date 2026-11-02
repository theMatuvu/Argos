package org.efrei.argosbackend.adapters.out.persistance.mapper;

import org.efrei.argosbackend.adapters.out.persistance.entity.StationEntity;
import org.efrei.argosbackend.domain.model.Station;
import org.springframework.stereotype.Component;

@Component
public class StationPersistenceMapper {

    public Station toDomain(StationEntity station) {
        return new Station(
                station.getId(),
                station.getName(),
                station.getLatitude(),
                station.getLongitude()
        );
    }
}
