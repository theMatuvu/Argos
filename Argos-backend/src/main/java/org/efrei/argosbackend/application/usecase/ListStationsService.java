package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.port.in.ListStationsUseCase;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListStationsService implements ListStationsUseCase {

    private final StationRepositoryPort stationRepository;

    public ListStationsService(StationRepositoryPort stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<Station> list() {
        return stationRepository.findAll();
    }
}
