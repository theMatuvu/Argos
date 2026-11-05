package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.efrei.argosbackend.adapters.in.rest.dto.response.EquipmentDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.EquipmentRestMapper;
import org.efrei.argosbackend.config.OpenApiConfig;
import org.efrei.argosbackend.domain.port.in.FindEquipmentDetailsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Équipements", description = "Fiches des équipements et historique de leurs incidents")
@RequestMapping("/equipments")
public class EquipmentController {

    private final FindEquipmentDetailsUseCase findEquipmentDetails;
    private final EquipmentRestMapper equipmentRestMapper;

    public EquipmentController(FindEquipmentDetailsUseCase findEquipmentDetails, EquipmentRestMapper equipmentRestMapper) {
        this.findEquipmentDetails = findEquipmentDetails;
        this.equipmentRestMapper = equipmentRestMapper;
    }

    @Operation(summary = "Consulter la fiche d'un équipement")
    @ApiResponse(responseCode = "200", description = "Fiche de l'équipement")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @GetMapping("/{equipmentId}")
    public EquipmentDetailsResponse findEquipmentDetails(@PathVariable UUID equipmentId) {
        return equipmentRestMapper.toResponse(findEquipmentDetails.find(equipmentId));
    }
}
