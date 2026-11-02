package org.efrei.argosbackend.adapters.in.rest.controller;

import org.efrei.argosbackend.adapters.in.rest.dto.response.EquipmentDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.EquipmentRestMapper;
import org.efrei.argosbackend.domain.port.in.FindEquipmentDetailsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    private final FindEquipmentDetailsUseCase findEquipmentDetails;
    private final EquipmentRestMapper equipmentRestMapper;

    public EquipmentController(FindEquipmentDetailsUseCase findEquipmentDetails, EquipmentRestMapper equipmentRestMapper) {
        this.findEquipmentDetails = findEquipmentDetails;
        this.equipmentRestMapper = equipmentRestMapper;
    }

    @GetMapping("/{equipmentId}")
    public EquipmentDetailsResponse findEquipmentDetails(@PathVariable UUID equipmentId) {
        return equipmentRestMapper.toResponse(findEquipmentDetails.find(equipmentId));
    }
}
