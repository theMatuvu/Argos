package org.efrei.argosbackend.adapters.out.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.efrei.argosbackend.domain.enums.EquipmentType;

import java.util.UUID;

@Entity
@Data
@Table(name = "equipments")
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String reference;

    private String label;

    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;

    @Column(name = "is_broken", nullable = false)
    private boolean isBroken;


}
