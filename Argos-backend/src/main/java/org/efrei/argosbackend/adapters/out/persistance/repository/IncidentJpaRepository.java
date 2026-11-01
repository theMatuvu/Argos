package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IncidentJpaRepository extends JpaRepository<IncidentEntity, UUID> {

    @Query("""
            select incident from IncidentEntity incident
            join fetch incident.equipment equipment
            join fetch equipment.station
            left join fetch incident.provider
            where equipment.id = :equipmentId
            """)
    List<IncidentEntity> findAllByEquipmentId(UUID equipmentId);
}
