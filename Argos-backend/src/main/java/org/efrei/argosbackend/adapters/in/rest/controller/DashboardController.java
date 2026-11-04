package org.efrei.argosbackend.adapters.in.rest.controller;

import org.efrei.argosbackend.adapters.in.rest.dto.response.DashboardResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.DashboardRestMapper;
import org.efrei.argosbackend.domain.port.in.LoadDashboardUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final LoadDashboardUseCase loadDashboard;
    private final DashboardRestMapper dashboardRestMapper;

    public DashboardController(LoadDashboardUseCase loadDashboard, DashboardRestMapper dashboardRestMapper) {
        this.loadDashboard = loadDashboard;
        this.dashboardRestMapper = dashboardRestMapper;
    }

    @GetMapping
    public DashboardResponse getDashboard() {
        return dashboardRestMapper.toResponse(loadDashboard.load());
    }
}
