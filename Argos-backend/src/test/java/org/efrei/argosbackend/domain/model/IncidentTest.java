package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.IncidentAlreadyEvaluatedException;
import org.efrei.argosbackend.domain.exception.IncidentAlreadyResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentNotResolvedException;
import org.efrei.argosbackend.domain.exception.IncidentWithoutProviderException;
import org.efrei.argosbackend.domain.exception.InvalidResolutionDateException;
import org.efrei.argosbackend.domain.model.enums.AttentionLevel;
import org.efrei.argosbackend.domain.model.enums.IncidentStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.efrei.argosbackend.TestData.DESCRIPTION;
import static org.efrei.argosbackend.TestData.NOW;
import static org.efrei.argosbackend.TestData.activeIncident;
import static org.efrei.argosbackend.TestData.assignedIncident;
import static org.efrei.argosbackend.TestData.equipment;
import static org.efrei.argosbackend.TestData.evaluatedIncident;
import static org.efrei.argosbackend.TestData.provider;
import static org.efrei.argosbackend.TestData.resolvedIncident;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentTest {

    private final Provider provider = provider();

    @Nested
    class Creation {

        @Test
        void declaresAnUnassignedActiveIncident() {
            Equipment equipment = equipment();

            Incident incident = Incident.declare(equipment, DESCRIPTION, NOW);

            assertNull(incident.id());
            assertEquals(equipment, incident.equipment());
            assertEquals(DESCRIPTION, incident.description());
            assertEquals(NOW, incident.openedAt());
            assertNull(incident.provider());
            assertFalse(incident.isResolved());
            assertFalse(incident.isEvaluated());
            assertEquals(IncidentStatus.UNASSIGNED, incident.status());
        }

        @Test
        void refusesAResolutionDateBeforeTheOpeningDate() {
            assertThrows(InvalidResolutionDateException.class, () -> new Incident(UUID.randomUUID(), equipment(), DESCRIPTION,
                    NOW, provider, NOW.minusMinutes(1), null, null));
        }

        @Test
        void acceptsAResolutionAtTheOpeningDate() {
            assertDoesNotThrow(() -> new Incident(UUID.randomUUID(), equipment(), DESCRIPTION, NOW, provider, NOW, null, null));
        }

        @Test
        void keepsEveryFieldWhenGivenAnId() {
            Incident incident = evaluatedIncident(provider, 4, NOW);
            UUID newId = UUID.randomUUID();

            Incident withId = incident.withId(newId);

            assertEquals(newId, withId.id());
            assertEquals(incident.rating(), withId.rating());
            assertEquals(incident.evaluatedAt(), withId.evaluatedAt());
            assertEquals(incident.resolvedAt(), withId.resolvedAt());
        }
    }

    @Nested
    class Status {

        @Test
        void isAssignedOnceAProviderIsSet() {
            assertEquals(IncidentStatus.ASSIGNED, assignedIncident(provider, NOW).status());
        }

        @Test
        void isResolvedOnceAResolutionDateIsSet() {
            assertEquals(IncidentStatus.RESOLVED, resolvedIncident(provider, NOW).status());
        }

        @Test
        void awaitsEvaluationWhenResolvedByAProviderWithoutRating() {
            assertTrue(resolvedIncident(provider, NOW).isAwaitingEvaluation());
        }

        @Test
        void doesNotAwaitEvaluationWhenStillActive() {
            assertFalse(assignedIncident(provider, NOW).isAwaitingEvaluation());
        }

        @Test
        void doesNotAwaitEvaluationWhenResolvedWithoutProvider() {
            assertFalse(activeIncident(NOW.minusHours(1)).resolve(NOW).isAwaitingEvaluation());
        }

        @Test
        void doesNotAwaitEvaluationOnceEvaluated() {
            assertFalse(evaluatedIncident(provider, 3, NOW).isAwaitingEvaluation());
        }
    }

    @Nested
    class Durations {

        @Test
        void measuresAnActiveIncidentUntilNow() {
            Incident incident = activeIncident(NOW.minusHours(5));

            assertEquals(Duration.ofHours(5), incident.openDuration(NOW));
            assertTrue(incident.resolutionTime().isEmpty());
        }

        @Test
        void measuresAResolvedIncidentUntilItsResolution() {
            Incident incident = activeIncident(NOW.minusHours(10)).resolve(NOW.minusHours(4));

            assertEquals(Duration.ofHours(6), incident.openDuration(NOW));
            assertEquals(Duration.ofHours(6), incident.resolutionTime().orElseThrow());
        }
    }

    @Nested
    class Attention {

        @ParameterizedTest(name = "ouvert depuis {0} min : {1}")
        @CsvSource({
                "0, GREEN",
                "2879, GREEN",
                "2880, ORANGE",
                "4319, ORANGE",
                "4320, RED",
                "10000, RED"
        })
        void dependsOnTheOpenDurationOfAnUnassignedIncident(long openMinutes, AttentionLevel expected) {
            Incident incident = activeIncident(NOW.minusMinutes(openMinutes));

            assertEquals(expected, incident.attentionLevel(NOW));
        }

        @Test
        void isAssignedWhateverTheDurationOnceAProviderIsSet() {
            Incident incident = assignedIncident(provider, NOW.minusDays(10));

            assertEquals(AttentionLevel.ASSIGNED, incident.attentionLevel(NOW));
        }
    }

    @Nested
    class Lifecycle {

        @Test
        void updatesTheDescriptionOfAnActiveIncident() {
            Incident updated = activeIncident(NOW).withDescription("Nouvelle description détaillée.");

            assertEquals("Nouvelle description détaillée.", updated.description());
        }

        @Test
        void refusesToUpdateTheDescriptionOfAResolvedIncident() {
            Incident resolved = resolvedIncident(provider, NOW);

            assertThrows(IncidentAlreadyResolvedException.class, () -> resolved.withDescription("Nouvelle description."));
        }

        @Test
        void reassignsAnActiveIncidentToAnotherProvider() {
            Provider otherProvider = provider("Autre prestataire");

            Incident reassigned = assignedIncident(provider, NOW).assignTo(otherProvider);

            assertEquals(otherProvider, reassigned.provider());
        }

        @Test
        void refusesToAssignAResolvedIncident() {
            Incident resolved = resolvedIncident(provider, NOW);

            assertThrows(IncidentAlreadyResolvedException.class, () -> resolved.assignTo(provider));
        }

        @Test
        void resolvesAnActiveIncidentAtTheGivenTime() {
            Incident resolved = assignedIncident(provider, NOW.minusHours(3)).resolve(NOW);

            assertEquals(NOW, resolved.resolvedAt());
            assertEquals(provider, resolved.provider());
        }

        @Test
        void refusesToResolveAnIncidentTwice() {
            Incident resolved = resolvedIncident(provider, NOW);

            assertThrows(IncidentAlreadyResolvedException.class, () -> resolved.resolve(NOW.plusHours(1)));
        }
    }

    @Nested
    class Evaluation {

        @Test
        void recordsTheRatingAndTheEvaluationTime() {
            LocalDateTime evaluationTime = NOW.plusHours(1);

            Incident evaluated = resolvedIncident(provider, NOW).evaluate(new Rating(4), evaluationTime);

            assertTrue(evaluated.isEvaluated());
            assertEquals(new Rating(4), evaluated.rating());
            assertEquals(evaluationTime, evaluated.evaluatedAt());
        }

        @Test
        void refusesToEvaluateAnActiveIncident() {
            Incident active = assignedIncident(provider, NOW);

            assertThrows(IncidentNotResolvedException.class, () -> active.evaluate(new Rating(4), NOW));
        }

        @Test
        void refusesToEvaluateAnIncidentResolvedWithoutProvider() {
            Incident resolvedWithoutProvider = activeIncident(NOW.minusHours(1)).resolve(NOW);

            assertThrows(IncidentWithoutProviderException.class, () -> resolvedWithoutProvider.evaluate(new Rating(4), NOW));
        }

        @Test
        void refusesToEvaluateAnIncidentTwice() {
            Incident evaluated = evaluatedIncident(provider, 4, NOW);

            assertThrows(IncidentAlreadyEvaluatedException.class, () -> evaluated.evaluate(new Rating(5), NOW.plusHours(1)));
        }
    }
}
