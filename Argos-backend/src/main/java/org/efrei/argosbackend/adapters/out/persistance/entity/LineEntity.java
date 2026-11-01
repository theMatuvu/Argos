package org.efrei.argosbackend.adapters.out.persistance.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transport_lines")
public class LineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String name;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(nullable = false, length = 7)
    private String textColor;

    @ElementCollection
    @CollectionTable(name = "line_segments", joinColumns = @JoinColumn(name = "line_id"))
    private List<LineSegmentEmbeddable> segments = new ArrayList<>();
}
