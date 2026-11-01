package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.model.NetworkMap;
import org.efrei.argosbackend.domain.port.in.LoadNetworkMapUseCase;
import org.efrei.argosbackend.domain.port.out.LineRepositoryPort;
import org.efrei.argosbackend.domain.port.out.StationRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class LoadNetworkMapService implements LoadNetworkMapUseCase {

    private final StationRepositoryPort stationRepository;
    private final LineRepositoryPort lineRepository;

    public LoadNetworkMapService(StationRepositoryPort stationRepository, LineRepositoryPort lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Override
    public NetworkMap load() {
        return new NetworkMap(stationRepository.findAll(), lineRepository.findAll());
    }
}
