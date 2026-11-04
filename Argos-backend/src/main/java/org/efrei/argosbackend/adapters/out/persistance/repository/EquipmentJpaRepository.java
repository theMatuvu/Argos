package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.EquipmentEntity;
import org.efrei.argosbackend.domain.model.enums.OperatingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentEntity, UUID> {

    @Query("""
            select equipment from EquipmentEntity equipment
            join fetch equipment.station station
            where station.id = :stationId
            order by equipment.type, equipment.reference
            """)
    List<EquipmentEntity> findAllByStationId(UUID stationId);

    @Query("""
            select equipment from EquipmentEntity equipment
            join fetch equipment.station
            where equipment.id = :equipmentId
            """)
    Optional<EquipmentEntity> findWithStationById(UUID equipmentId);

    long countByOperatingStatus(OperatingStatus operatingStatus);

    @Modifying
    @Query("update EquipmentEntity equipment set equipment.operatingStatus = :operatingStatus where equipment.id = :equipmentId")
    void updateOperatingStatus(UUID equipmentId, OperatingStatus operatingStatus);
}
