package org.efrei.argosbackend.domain.port.out;

import org.efrei.argosbackend.domain.model.Station;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationRepositoryPort {
    List<Station> findAll();

    Optional<Station> findById(UUID stationId);
}
