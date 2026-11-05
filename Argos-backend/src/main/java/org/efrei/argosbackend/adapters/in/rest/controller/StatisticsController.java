package org.efrei.argosbackend.adapters.in.rest.controller;

import org.efrei.argosbackend.adapters.in.rest.dto.response.StatisticsResponse;
import org.efrei.argosbackend.adapters.in.rest.mapper.StatisticsRestMapper;
import org.efrei.argosbackend.domain.port.in.LoadStatisticsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final LoadStatisticsUseCase loadStatistics;
    private final StatisticsRestMapper statisticsRestMapper;

    public StatisticsController(LoadStatisticsUseCase loadStatistics, StatisticsRestMapper statisticsRestMapper) {
        this.loadStatistics = loadStatistics;
        this.statisticsRestMapper = statisticsRestMapper;
    }

    @GetMapping
    public StatisticsResponse getStatistics() {
        return statisticsRestMapper.toResponse(loadStatistics.load());
    }
}
