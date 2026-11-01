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
import org.efrei.argosbackend.domain.enums.IncidentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "incidents")
public class IncidentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentEntity equipment;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "incident_status", nullable = false)
    private IncidentStatus incidentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;


}
