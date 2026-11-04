package org.efrei.argosbackend.adapters.in.rest.controller;

import jakarta.validation.Valid;
import org.efrei.argosbackend.adapters.in.rest.dto.request.AssignProviderRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.request.EvaluateIncidentRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.request.IncidentDescriptionRequest;
import org.efrei.argosbackend.adapters.in.rest.dto.response.IncidentDetailsResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.IncidentRestMapper;
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

    @PostMapping("/equipments/{equipmentId}/incidents")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentDetailsResponse declare(@PathVariable UUID equipmentId, @RequestBody @Valid IncidentDescriptionRequest request) {
        return incidentRestMapper.toResponse(declareIncident.declare(incidentRestMapper.toCommand(equipmentId, request)));
    }

    @GetMapping("/incidents/resolved")
    public List<IncidentDetailsResponse> listResolved() {
        return listResolvedIncidents.list().stream().map(incidentRestMapper::toResponse).toList();
    }

    @GetMapping("/incidents/{incidentId}")
    public IncidentDetailsResponse findDetails(@PathVariable UUID incidentId) {
        return incidentRestMapper.toResponse(findIncidentDetails.find(incidentId));
    }

    @PatchMapping("/incidents/{incidentId}")
    public IncidentDetailsResponse updateDescription(@PathVariable UUID incidentId, @RequestBody @Valid IncidentDescriptionRequest request) {
        return incidentRestMapper.toResponse(updateIncidentDescription.update(incidentId, request.description()));
    }

    @PutMapping("/incidents/{incidentId}/provider")
    public IncidentDetailsResponse assignProvider(@PathVariable UUID incidentId, @RequestBody @Valid AssignProviderRequest request) {
        return incidentRestMapper.toResponse(assignProvider.assign(incidentId, request.providerId()));
    }

    @PostMapping("/incidents/{incidentId}/resolution")
    public IncidentDetailsResponse resolve(@PathVariable UUID incidentId) {
        return incidentRestMapper.toResponse(resolveIncident.resolve(incidentId));
    }

    @PostMapping("/incidents/{incidentId}/evaluation")
    public IncidentDetailsResponse evaluate(@PathVariable UUID incidentId, @RequestBody @Valid EvaluateIncidentRequest request) {
        return incidentRestMapper.toResponse(evaluateIncident.evaluate(incidentId, request.rating()));
    }
}
