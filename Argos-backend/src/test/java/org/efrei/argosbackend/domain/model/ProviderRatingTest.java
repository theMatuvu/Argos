package org.efrei.argosbackend.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProviderRatingTest {

    @Test
    void hasNoAverageWithoutEvaluation() {
        ProviderRating rating = ProviderRating.of(List.of());

        assertEquals(0, rating.evaluationCount());
        assertTrue(rating.average().isEmpty());
    }

    @Test
    void averagesEveryEvaluation() {
        ProviderRating rating = ProviderRating.of(List.of(new Rating(5), new Rating(4), new Rating(2)));

        assertEquals(3, rating.evaluationCount());
        assertEquals(11.0 / 3, rating.average().getAsDouble(), 1e-9);
    }
}
