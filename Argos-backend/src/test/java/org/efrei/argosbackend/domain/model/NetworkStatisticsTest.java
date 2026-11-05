package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import static org.efrei.argosbackend.TestData.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkStatisticsTest {

    @Test
    void derivesTheTotalsFromTheDetailedCounts() {
        NetworkStatistics statistics = new NetworkStatistics(
                Map.of(AttentionLevel.GREEN, 3L, AttentionLevel.ORANGE, 1L, AttentionLevel.RED, 2L, AttentionLevel.ASSIGNED, 4L),
                List.of(
                        new DailyIncidentActivity(NOW.toLocalDate().minusDays(1), 2, 1),
                        new DailyIncidentActivity(NOW.toLocalDate(), 3, 4)
                ),
                List.of(),
                List.of(),
                OptionalDouble.empty(),
                ProviderRating.of(List.of())
        );

        assertEquals(10, statistics.activeIncidentCount());
        assertEquals(6, statistics.unassignedIncidentCount());
        assertEquals(5, statistics.openedIncidentCount());
        assertEquals(5, statistics.resolvedIncidentCount());
    }

    @Test
    void countsEveryActiveIncidentAsUnassignedWhenNoneIsAssigned() {
        NetworkStatistics statistics = new NetworkStatistics(Map.of(AttentionLevel.RED, 2L), List.of(), List.of(), List.of(),
                OptionalDouble.empty(), ProviderRating.of(List.of()));

        assertEquals(2, statistics.unassignedIncidentCount());
    }
}
