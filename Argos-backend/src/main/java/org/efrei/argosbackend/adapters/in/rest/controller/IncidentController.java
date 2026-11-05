package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.efrei.argosbackend.adapters.in.rest.dto.request.AssignProviderRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.request.EvaluateIncidentRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.request.IncidentDescriptionRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.IncidentRestMapper;
import org.efrei.argosbackend.config.OpenApiConfig;
import org.efrei.argosbackend.domain.port.in.AssignProviderUseCase;
import org.efrei.argosbackend.domain.port.in.DeclareIncidentUseCase;
import org.efrei.argosbackend.domain.port.in.EvaluateIncidentUseCase;
import org.efrei.argosbackend.domain.port.in.FindIncidentDetailsUseCase;
import org.efrei.argosbackend.domain.port.in.ListResolvedIncidentsUseCase;
import org.efrei.argosbackend.domain.port.in.ResolveIncidentUseCase;
import org.efrei.argosbackend.domain.port.in.UpdateIncidentDescriptionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Incidents", description = "Cycle de vie des incidents : déclaration, affectation, résolution et évaluation")
public class IncidentController {

    private final DeclareIncidentUseCase declareIncident;
    private final FindIncidentDetailsUseCase findIncidentDetails;
    private final UpdateIncidentDescriptionUseCase updateIncidentDescription;
    private final AssignProviderUseCase assignProvider;
    private final ResolveIncidentUseCase resolveIncident;
    private final ListResolvedIncidentsUseCase listResolvedIncidents;
    private final EvaluateIncidentUseCase evaluateIncident;
    private final IncidentRestMapper incidentRestMapper;

    public IncidentController(DeclareIncidentUseCase declareIncident, FindIncidentDetailsUseCase findIncidentDetails,
                              UpdateIncidentDescriptionUseCase updateIncidentDescription, AssignProviderUseCase assignProvider,
                              ResolveIncidentUseCase resolveIncident, ListResolvedIncidentsUseCase listResolvedIncidents,
                              EvaluateIncidentUseCase evaluateIncident, IncidentRestMapper incidentRestMapper) {
        this.declareIncident = declareIncident;
        this.findIncidentDetails = findIncidentDetails;
        this.updateIncidentDescription = updateIncidentDescription;
        this.assignProvider = assignProvider;
        this.resolveIncident = resolveIncident;
        this.listResolvedIncidents = listResolvedIncidents;
        this.evaluateIncident = evaluateIncident;
        this.incidentRestMapper = incidentRestMapper;
    }

    @Operation(summary = "Déclarer un incident sur un équipement")
    @ApiResponse(responseCode = "201", description = "Incident déclaré")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PostMapping("/equipments/{equipmentId}/incidents")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentDetailsResponse declare(@PathVariable UUID equipmentId, @RequestBody @Valid IncidentDescriptionRequest request) {
        return incidentRestMapper.toResponse(declareIncident.declare(incidentRestMapper.toCommand(equipmentId, request)));
    }

    @Operation(summary = "Lister les incidents résolus, du plus récent au plus ancien")
    @GetMapping("/incidents/resolved")
    public List<IncidentDetailsResponse> listResolved() {
        return listResolvedIncidents.list().stream().map(incidentRestMapper::toResponse).toList();
    }

    @Operation(summary = "Consulter le détail d'un incident")
    @ApiResponse(responseCode = "200", description = "Détail de l'incident")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @GetMapping("/incidents/{incidentId}")
    public IncidentDetailsResponse findDetails(@PathVariable UUID incidentId) {
        return incidentRestMapper.toResponse(findIncidentDetails.find(incidentId));
    }

    @Operation(summary = "Modifier la description d'un incident non résolu")
    @ApiResponse(responseCode = "200", description = "Incident modifié")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @PatchMapping("/incidents/{incidentId}")
    public IncidentDetailsResponse updateDescription(@PathVariable UUID incidentId, @RequestBody @Valid IncidentDescriptionRequest request) {
        return incidentRestMapper.toResponse(updateIncidentDescription.update(incidentId, request.description()));
    }

    @Operation(summary = "Affecter ou réaffecter un prestataire")
    @ApiResponse(responseCode = "200", description = "Prestataire affecté")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.FORBIDDEN)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @PutMapping("/incidents/{incidentId}/provider")
    public IncidentDetailsResponse assignProvider(@PathVariable UUID incidentId, @RequestBody @Valid AssignProviderRequest request) {
        return incidentRestMapper.toResponse(assignProvider.assign(incidentId, request.providerId()));
    }

    @Operation(summary = "Déclarer un incident résolu")
    @ApiResponse(responseCode = "200", description = "Incident résolu")
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.FORBIDDEN)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @PostMapping("/incidents/{incidentId}/resolution")
    public IncidentDetailsResponse resolve(@PathVariable UUID incidentId) {
        return incidentRestMapper.toResponse(resolveIncident.resolve(incidentId));
    }

    @Operation(summary = "Évaluer l'intervention du prestataire")
    @ApiResponse(responseCode = "200", description = "Évaluation enregistrée")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.FORBIDDEN)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @PostMapping("/incidents/{incidentId}/evaluation")
    public IncidentDetailsResponse evaluate(@PathVariable UUID incidentId, @RequestBody @Valid EvaluateIncidentRequest request) {
        return incidentRestMapper.toResponse(evaluateIncident.evaluate(incidentId, request.rating()));
    }
}
