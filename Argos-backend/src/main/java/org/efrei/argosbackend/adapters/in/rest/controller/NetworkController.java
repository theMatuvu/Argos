package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.efrei.argosbackend.adapters.in.rest.dto.response.NetworkMapResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.NetworkRestMapper;
import org.efrei.argosbackend.domain.port.in.LoadNetworkMapUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Réseau", description = "Lignes et stations du réseau pour la cartographie")
@RequestMapping("/network")
public class NetworkController {

    private final LoadNetworkMapUseCase loadNetworkMap;
    private final NetworkRestMapper networkRestMapper;

    public NetworkController(LoadNetworkMapUseCase loadNetworkMap, NetworkRestMapper networkRestMapper) {
        this.loadNetworkMap = loadNetworkMap;
        this.networkRestMapper = networkRestMapper;
    }

    @Operation(summary = "Charger le plan du réseau")
    @GetMapping
    public NetworkMapResponse getNetworkMap() {
        return networkRestMapper.toResponse(loadNetworkMap.load());
    }
}
