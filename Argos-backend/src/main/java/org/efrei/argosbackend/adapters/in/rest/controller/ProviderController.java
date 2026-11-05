package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.dto.response.ProviderSummaryResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.ProviderDetailsRestMapper;
import org.efrei.argosbackend.adapters.in.rest.mapper.ProviderRestMapper;
import org.efrei.argosbackend.config.OpenApiConfig;
import org.efrei.argosbackend.domain.port.in.FindProviderDetailsUseCase;
import org.efrei.argosbackend.domain.port.in.ListProvidersUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Prestataires", description = "Prestataires et notes de leurs interventions")
@RequestMapping("/providers")
public class ProviderController {

    private final ListProvidersUseCase listProviders;
    private final FindProviderDetailsUseCase findProviderDetails;
    private final ProviderRestMapper providerRestMapper;
    private final ProviderDetailsRestMapper providerDetailsRestMapper;

    public ProviderController(ListProvidersUseCase listProviders, FindProviderDetailsUseCase findProviderDetails,
                              ProviderRestMapper providerRestMapper, ProviderDetailsRestMapper providerDetailsRestMapper) {
        this.listProviders = listProviders;
        this.findProviderDetails = findProviderDetails;
        this.providerRestMapper = providerRestMapper;
        this.providerDetailsRestMapper = providerDetailsRestMapper;
    }

    @Operation(summary = "Lister les prestataires avec leur note moyenne")
    @GetMapping
    public List<ProviderSummaryResponse> list() {
        return listProviders.list().stream().map(providerRestMapper::toResponse).toList();
    }

    @Operation(summary = "Consulter la fiche d'un prestataire")
    @ApiResponse(responseCode = "200", description = "Fiche du prestataire")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @GetMapping("/{providerId}")
    public ProviderDetailsResponse findDetails(@PathVariable UUID providerId) {
        return providerDetailsRestMapper.toResponse(findProviderDetails.find(providerId));
    }
}
