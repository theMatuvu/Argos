package org.efrei.argosbackend.domain.model;

import java.util.List;
import java.util.UUID;

public record Line(UUID id, String name, String color, String textColor, List<LineSegment> segments) {

    public Line {
        segments = List.copyOf(segments);
    }
}
