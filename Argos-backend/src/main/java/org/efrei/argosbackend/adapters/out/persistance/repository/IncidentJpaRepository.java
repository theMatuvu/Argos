package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.IncidentEntity;
import org.efrei.argosbackend.adapters.out.persistance.projection.EquipmentTypeIncidentCountView;
import org.efrei.argosbackend.adapters.out.persistance.projection.StationIncidentCountView;
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

    @Query("""
            select equipment.type as equipmentType, count(incident) as incidentCount
            from IncidentEntity incident
            join incident.equipment equipment
            group by equipment.type
            """)
    List<EquipmentTypeIncidentCountView> countGroupedByEquipmentType();

    @Query(value = """
            SELECT station.name AS stationName, COUNT(incident.id) AS incidentCount
            FROM incidents incident
            JOIN equipments equipment ON equipment.id = incident.equipment_id
            JOIN stations station ON station.id = equipment.station_id
            GROUP BY station.id, station.name
            ORDER BY incidentCount DESC, station.name
            LIMIT :limit
            """, nativeQuery = true)
    List<StationIncidentCountView> findMostAffectedStations(int limit);
}
