package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.ProviderUnderSurveillanceException;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProviderSurveillanceTest {

    private static final LocalDateTime REFERENCE_DATE = LocalDateTime.of(2026, 10, 1, 9, 0);
    private static final Provider PROVIDER = new Provider(UUID.randomUUID(), "Dupont Réseaux", "01 00 00 00 00");
    private static final Equipment EQUIPMENT = new Equipment(UUID.randomUUID(), "PTQ-TEST-1", "Portique",
            EquipmentType.TICKET_GATE, new Station(UUID.randomUUID(), "Station test", 48.85, 2.35), OperatingStatus.IN_SERVICE);

    static Stream<Arguments> referenceExamples() {
        return Stream.of(
                Arguments.of(List.of(4, 1, 1, 1), null, false),
                Arguments.of(List.of(3, 3, 3, 3, 3), 3.0, false),
                Arguments.of(List.of(3, 3, 3, 3, 3, 2), 2.8, true),
                Arguments.of(List.of(3, 3, 3, 3, 3, 2, 5), 3.2, false),
                Arguments.of(List.of(1, 5, 5, 5, 5, 5), 5.0, false),
                Arguments.of(List.of(2, 3, 3, 3, 3), 2.8, true),
                Arguments.of(List.of(1, 1, 5, 5, 5), 3.4, false)
        );
    }

    @ParameterizedTest(name = "notes {0} : moyenne {1}, surveillé {2}")
    @MethodSource("referenceExamples")
    void appliesTheReferenceExamples(List<Integer> ratingsFromOldestToMostRecent, Double expectedAverage, boolean expectedUnderSurveillance) {
        ProviderSurveillance surveillance = ProviderSurveillance.of(evaluatedInOrder(ratingsFromOldestToMostRecent));

        if (expectedAverage == null) {
            assertTrue(surveillance.recentAverage().isEmpty());
        } else {
            assertEquals(expectedAverage, surveillance.recentAverage().getAsDouble(), 1e-9);
        }
        assertEquals(expectedUnderSurveillance, surveillance.isUnderSurveillance());
    }

    @Test
    void sortsEvaluationsByDateWhateverTheInputOrder() {
        List<Incident> incidents = new ArrayList<>(evaluatedInOrder(List.of(3, 3, 3, 3, 3, 2)));
        Collections.reverse(incidents);

        assertTrue(ProviderSurveillance.of(incidents).isUnderSurveillance());
    }

    @Test
    void breaksTiesOnEvaluationDateByDescendingIdentifier() {
        LocalDateTime oldestDate = REFERENCE_DATE;
        List<Incident> incidents = new ArrayList<>();
        incidents.add(evaluated(new UUID(0, 1), 5, oldestDate));
        incidents.add(evaluated(new UUID(0, 2), 1, oldestDate));
        IntStream.rangeClosed(1, 4).forEach(day -> incidents.add(evaluated(UUID.randomUUID(), 3, oldestDate.plusDays(day))));

        ProviderSurveillance surveillance = ProviderSurveillance.of(incidents);

        assertEquals(2.6, surveillance.recentAverage().getAsDouble(), 1e-9);
        assertTrue(surveillance.isUnderSurveillance());
    }

    @Test
    void ignoresIncidentsWithoutEvaluation() {
        List<Incident> incidents = new ArrayList<>(evaluatedInOrder(List.of(1, 1, 1, 1)));
        incidents.add(new Incident(UUID.randomUUID(), EQUIPMENT, "Incident résolu sans note.", REFERENCE_DATE,
                PROVIDER, REFERENCE_DATE.plusHours(1), null, null));

        assertFalse(ProviderSurveillance.of(incidents).isUnderSurveillance());
    }

    @Test
    void refusesAnUnconfirmedAssignmentToAProviderUnderSurveillance() {
        ProviderSurveillance surveillance = ProviderSurveillance.of(evaluatedInOrder(List.of(2, 2, 2, 2, 2)));

        ProviderUnderSurveillanceException exception =
                assertThrows(ProviderUnderSurveillanceException.class, () -> surveillance.ensureAssignable(PROVIDER, false));
        assertTrue(exception.getMessage().contains("surveillance"));
        assertDoesNotThrow(() -> surveillance.ensureAssignable(PROVIDER, true));
    }

    @Test
    void acceptsAnUnconfirmedAssignmentToAProviderNotUnderSurveillance() {
        ProviderSurveillance surveillance = ProviderSurveillance.of(evaluatedInOrder(List.of(4, 4, 4, 4, 4)));

        assertDoesNotThrow(() -> surveillance.ensureAssignable(PROVIDER, false));
    }

    static List<Incident> evaluatedInOrder(List<Integer> ratingsFromOldestToMostRecent) {
        return IntStream.range(0, ratingsFromOldestToMostRecent.size())
                .mapToObj(index -> evaluated(UUID.randomUUID(), ratingsFromOldestToMostRecent.get(index), REFERENCE_DATE.plusDays(index)))
                .toList();
    }

    private static Incident evaluated(UUID id, int rating, LocalDateTime evaluatedAt) {
        LocalDateTime resolvedAt = evaluatedAt.minusHours(2);
        return new Incident(id, EQUIPMENT, "Incident de test évalué.", resolvedAt.minusDays(1), PROVIDER,
                resolvedAt, new Rating(rating), evaluatedAt);
    }
}
