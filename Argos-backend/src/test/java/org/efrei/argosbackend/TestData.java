package org.efrei.argosbackend;

import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.Provider;
import org.efrei.argosbackend.domain.model.Rating;
import org.efrei.argosbackend.domain.model.Station;
import org.efrei.argosbackend.domain.model.enums.EquipmentType;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class TestData {

    public static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 5, 9, 0);
    public static final String DESCRIPTION = "Portique bloqué en position fermée.";

    private TestData() {
    }

    public static Clock fixedClock() {
        return Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static Station station() {
        return station("Châtelet");
    }

    public static Station station(String name) {
        return new Station(UUID.randomUUID(), name, 48.8584, 2.3470);
    }

    public static Equipment equipment() {
        return equipment(EquipmentType.TICKET_GATE);
    }

    public static Equipment equipment(EquipmentType type) {
        return new Equipment(UUID.randomUUID(), "PTQ-CHATELET-1", "Portiques de validation", type, station(), OperatingStatus.IN_SERVICE);
    }

    public static Provider provider() {
        return provider("Dupont Réseaux");
    }

    public static Provider provider(String name) {
        return new Provider(UUID.randomUUID(), name, "01 00 00 00 00");
    }

    public static Incident activeIncident(LocalDateTime openedAt) {
        return activeIncident(equipment(), openedAt);
    }

    public static Incident activeIncident(Equipment equipment, LocalDateTime openedAt) {
        return Incident.declare(equipment, DESCRIPTION, openedAt).withId(UUID.randomUUID());
    }

    public static Incident assignedIncident(Provider provider, LocalDateTime openedAt) {
        return activeIncident(openedAt).assignTo(provider);
    }

    public static Incident resolvedIncident(Provider provider, LocalDateTime resolvedAt) {
        return assignedIncident(provider, resolvedAt.minusDays(1)).resolve(resolvedAt);
    }

    public static Incident evaluatedIncident(Provider provider, int rating, LocalDateTime evaluatedAt) {
        return resolvedIncident(provider, evaluatedAt.minusHours(2)).evaluate(new Rating(rating), evaluatedAt);
    }
}
