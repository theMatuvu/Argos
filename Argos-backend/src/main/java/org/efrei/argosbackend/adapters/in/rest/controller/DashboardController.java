package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.efrei.argosbackend.adapters.in.rest.dto.response.DashboardResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.DashboardRestMapper;
import org.efrei.argosbackend.domain.port.in.LoadDashboardUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Tableau de bord", description = "Incidents actifs et indicateurs de supervision")
@RequestMapping("/dashboard")
public class DashboardController {

    private final LoadDashboardUseCase loadDashboard;
    private final DashboardRestMapper dashboardRestMapper;

    public DashboardController(LoadDashboardUseCase loadDashboard, DashboardRestMapper dashboardRestMapper) {
        this.loadDashboard = loadDashboard;
        this.dashboardRestMapper = dashboardRestMapper;
    }

    @Operation(summary = "Charger le tableau de bord des incidents actifs")
    @GetMapping
    public DashboardResponse getDashboard() {
        return dashboardRestMapper.toResponse(loadDashboard.load());
    }
}
