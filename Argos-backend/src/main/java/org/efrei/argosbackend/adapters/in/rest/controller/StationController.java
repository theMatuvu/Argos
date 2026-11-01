package org.efrei.argosbackend.adapters.in.rest.controller;

import org.efrei.argosbackend.adapters.in.rest.dto.response.StationEquipmentsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.StationResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.StationRestMapper;
import org.efrei.argosbackend.domain.port.in.FindStationEquipmentsUseCase;
import org.efrei.argosbackend.domain.port.in.ListStationsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final ListStationsUseCase listStations;
    private final FindStationEquipmentsUseCase findStationEquipments;
    private final StationRestMapper stationRestMapper;

    public StationController(ListStationsUseCase listStations, FindStationEquipmentsUseCase findStationEquipments, StationRestMapper stationRestMapper) {
        this.listStations = listStations;
        this.findStationEquipments = findStationEquipments;
        this.stationRestMapper = stationRestMapper;
    }

    @GetMapping
    public List<StationResponse> listStations() {
        return listStations.list().stream().map(stationRestMapper::toResponse).toList();
    }

    @GetMapping("/{stationId}/equipments")
    public StationEquipmentsResponse findStationEquipments(@PathVariable UUID stationId) {
        return stationRestMapper.toResponse(findStationEquipments.find(stationId));
    }
}
