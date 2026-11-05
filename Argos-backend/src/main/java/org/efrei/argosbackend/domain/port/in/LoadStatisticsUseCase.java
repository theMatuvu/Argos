package org.efrei.argosbackend.domain.port.in;

import org.efrei.argosbackend.domain.model.NetworkStatistics;

public interface LoadStatisticsUseCase {
    NetworkStatistics load();
}
