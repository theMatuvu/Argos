package org.efrei.argosbackend.domain.model;

import java.time.LocalDate;

public record DailyIncidentActivity(LocalDate day, long openedCount, long resolvedCount) {
}
