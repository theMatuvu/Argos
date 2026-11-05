package org.efrei.argosbackend.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.efrei.argosbackend.adapters.in.rest.dto.response.StatisticsResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.StatisticsRestMapper;
import org.efrei.argosbackend.domain.port.in.LoadStatisticsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Statistiques", description = "Indicateurs et agrégats sur l'activité du réseau")
@RequestMapping("/statistics")
public class StatisticsController {

    private final LoadStatisticsUseCase loadStatistics;
    private final StatisticsRestMapper statisticsRestMapper;

    public StatisticsController(LoadStatisticsUseCase loadStatistics, StatisticsRestMapper statisticsRestMapper) {
        this.loadStatistics = loadStatistics;
        this.statisticsRestMapper = statisticsRestMapper;
    }

    @Operation(summary = "Charger les statistiques du réseau")
    @GetMapping
    public StatisticsResponse getStatistics() {
        return statisticsRestMapper.toResponse(loadStatistics.load());
    }
}
