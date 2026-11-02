package org.efrei.argosbackend.application.usecase;

import org.efrei.argosbackend.domain.exception.EquipmentNotFoundException;
import org.efrei.argosbackend.domain.model.Equipment;
import org.efrei.argosbackend.domain.model.EquipmentDetails;
import org.efrei.argosbackend.domain.model.Incident;
import org.efrei.argosbackend.domain.port.in.FindEquipmentDetailsUseCase;
import org.efrei.argosbackend.domain.port.out.EquipmentRepositoryPort;
import org.efrei.argosbackend.domain.port.out.IncidentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class FindEquipmentDetailsService implements FindEquipmentDetailsUseCase {

    private static final Comparator<Incident> MOST_RECENTLY_OPENED_FIRST =
            Comparator.comparing(Incident::openedAt).reversed();

    private final EquipmentRepositoryPort equipmentRepository;
    private final IncidentRepositoryPort incidentRepository;

    public FindEquipmentDetailsService(EquipmentRepositoryPort equipmentRepository, IncidentRepositoryPort incidentRepository) {
        this.equipmentRepository = equipmentRepository;
        this.incidentRepository = incidentRepository;
    }

    @Override
    public EquipmentDetails find(UUID equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
        return new EquipmentDetails(equipment, incidentsByOpeningDate(equipmentId));
    }

    private List<Incident> incidentsByOpeningDate(UUID equipmentId) {
        return incidentRepository.findAllByEquipmentId(equipmentId).stream()
                .sorted(MOST_RECENTLY_OPENED_FIRST)
                .toList();
    }
}
