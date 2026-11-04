package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
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

    @Query("""
            select incident from IncidentEntity incident
            join fetch incident.equipment equipment
            join fetch equipment.station
            left join fetch incident.provider
            where incident.id = :incidentId
            """)
    Optional<IncidentEntity> findWithDetailsById(UUID incidentId);

    @Query("""
            select incident from IncidentEntity incident
            join fetch incident.equipment equipment
            join fetch equipment.station
            left join fetch incident.provider
            where incident.resolvedAt is null
            """)
    List<IncidentEntity> findAllActive();

    @Query("""
            select incident from IncidentEntity incident
            join fetch incident.equipment equipment
            join fetch equipment.station
            left join fetch incident.provider
            where incident.resolvedAt is not null
            """)
    List<IncidentEntity> findAllResolved();

    @Query("""
            select incident from IncidentEntity incident
            join fetch incident.equipment equipment
            join fetch equipment.station
            join fetch incident.provider provider
            where provider.id = :providerId and incident.resolvedAt is not null
            """)
    List<IncidentEntity> findAllResolvedByProviderId(UUID providerId);

    boolean existsByEquipmentIdAndResolvedAtIsNull(UUID equipmentId);
}
