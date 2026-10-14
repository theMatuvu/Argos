package org.efrei.argosbackend.adapters.out.persistance.repository;

import org.efrei.argosbackend.adapters.out.persistance.entity.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentEntity, UUID> {
    List<EquipmentEntity> findAllByStation_Name(String stationName);
}
