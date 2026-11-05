package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.EquipmentNotFoundException;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.model.IncidentDetails;
import org.efrei.argosbackend.domain.model.ProviderSurveillance;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.efrei.argosbackend.domain.port.in.DeclareIncidentCommand;
import org.efrei.argosbackend.domain.port.in.DeclareIncidentUseCase;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class DeclareIncidentService implements DeclareIncidentUseCase {

    private final EquipmentRepositoryPort equipmentRepository;
    private final IncidentRepositoryPort incidentRepository;
    private final Clock clock;

    public DeclareIncidentService(EquipmentRepositoryPort equipmentRepository, IncidentRepositoryPort incidentRepository, Clock clock) {
        this.equipmentRepository = equipmentRepository;
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public IncidentDetails declare(DeclareIncidentCommand command) {
        Equipment equipment = equipmentRepository.findById(command.equipmentId())
                .orElseThrow(() -> new EquipmentNotFoundException(command.equipmentId()));
        LocalDateTime now = LocalDateTime.now(clock);
        Incident incident = incidentRepository.save(Incident.declare(equipment, command.description(), now));
        equipmentRepository.updateOperatingStatus(equipment.id(), OperatingStatus.OUT_OF_SERVICE);
        return IncidentDetails.of(incident, now, ProviderSurveillance.none());
    }
}
