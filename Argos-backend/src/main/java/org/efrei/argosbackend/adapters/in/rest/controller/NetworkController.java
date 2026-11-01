package org.efrei.argosbackend.adapters.in.rest.controller;

import org.efrei.argosbackend.adapters.in.rest.dto.response.NetworkMapResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.NetworkRestMapper;
import org.efrei.argosbackend.domain.port.in.LoadNetworkMapUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/network")
public class NetworkController {

    private final LoadNetworkMapUseCase loadNetworkMap;
    private final NetworkRestMapper networkRestMapper;

    public NetworkController(LoadNetworkMapUseCase loadNetworkMap, NetworkRestMapper networkRestMapper) {
        this.loadNetworkMap = loadNetworkMap;
        this.networkRestMapper = networkRestMapper;
    }

    @GetMapping
    public NetworkMapResponse getNetworkMap() {
        return networkRestMapper.toResponse(loadNetworkMap.load());
    }
}
