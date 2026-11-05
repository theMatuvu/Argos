package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.IncidentNotFoundException;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.in.ResolveIncidentUseCase;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ResolveIncidentService implements ResolveIncidentUseCase {

    private final IncidentRepositoryPort incidentRepository;
    private final EquipmentRepositoryPort equipmentRepository;
    private final ProviderSurveillanceCalculator providerSurveillanceCalculator;
    private final Clock clock;

    public ResolveIncidentService(IncidentRepositoryPort incidentRepository, EquipmentRepositoryPort equipmentRepository,
                                  ProviderSurveillanceCalculator providerSurveillanceCalculator, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.providerSurveillanceCalculator = providerSurveillanceCalculator;
        this.equipmentRepository = equipmentRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public IncidentDetails resolve(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        LocalDateTime now = LocalDateTime.now(clock);
        Incident resolved = incidentRepository.save(incident.resolve(now));
        putEquipmentBackInServiceIfNoActiveIncident(resolved.equipment().id());
        return IncidentDetails.of(resolved, now, providerSurveillanceCalculator.of(resolved.provider()));
    }

    private void putEquipmentBackInServiceIfNoActiveIncident(UUID equipmentId) {
        if (!incidentRepository.existsActiveByEquipmentId(equipmentId)) {
            equipmentRepository.updateOperatingStatus(equipmentId, OperatingStatus.IN_SERVICE);
        }
    }
}
