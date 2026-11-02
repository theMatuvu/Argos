package org.efrei.argosbackend.adapters.out.persistance.adapter;

import org.efrei.argosbackend.adapters.out.persistance.mapper.StationPersistenceMapper;
import org.efrei.argosbackend.adapters.out.persistance.repository.StationJpaRepository;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StationPersistenceAdapter implements StationRepositoryPort {

    private final StationJpaRepository stationJpaRepository;
    private final StationPersistenceMapper stationPersistenceMapper;

    public StationPersistenceAdapter(StationJpaRepository stationJpaRepository, StationPersistenceMapper stationPersistenceMapper) {
        this.stationJpaRepository = stationJpaRepository;
        this.stationPersistenceMapper = stationPersistenceMapper;
    }

    @Override
    public List<Station> findAll() {
        return stationJpaRepository.findAllByOrderByNameAsc().stream()
                .map(stationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Station> findById(UUID stationId) {
        return stationJpaRepository.findById(stationId).map(stationPersistenceMapper::toDomain);
    }
}
