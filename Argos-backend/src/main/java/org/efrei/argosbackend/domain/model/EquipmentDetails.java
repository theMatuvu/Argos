package org.efrei.argosbackend.domain.model;

import java.util.List;

public record EquipmentDetails(Equipment equipment, List<Incident> incidents) {

    public EquipmentDetails {
        incidents = List.copyOf(incidents);
    }

    public List<Incident> activeIncidents() {
        return incidents.stream().filter(incident -> !incident.isResolved()).toList();
    }

    public List<Incident> pastIncidents() {
        return incidents.stream().filter(Incident::isResolved).toList();
    }
}
