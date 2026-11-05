package org.efrei.argosbackend.adapters.in.rest.dto.response;

import java.time.LocalDate;

public record DailyIncidentActivityResponse(LocalDate day, long openedCount, long resolvedCount) {
}
