package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.model.enums.IncidentStatus;

import java.util.List;

public record Dashboard(
        List<IncidentDetails> activeIncidents,
        List<IncidentDetails> incidentsAwaitingEvaluation,
        long outOfServiceEquipmentCount
) {

    public Dashboard {
        activeIncidents = List.copyOf(activeIncidents);
        incidentsAwaitingEvaluation = List.copyOf(incidentsAwaitingEvaluation);
    }

    public long unassignedCount() {
        return countActiveWithStatus(IncidentStatus.UNASSIGNED);
    }

    public long assignedCount() {
        return countActiveWithStatus(IncidentStatus.ASSIGNED);
    }

    private long countActiveWithStatus(IncidentStatus status) {
        return activeIncidents.stream().filter(details -> details.incident().status() == status).count();
    }
}
