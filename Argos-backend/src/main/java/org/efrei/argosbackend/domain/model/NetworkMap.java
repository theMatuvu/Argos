package org.efrei.argosbackend.domain.model;

import java.util.List;

public record NetworkMap(List<Station> stations, List<Line> lines) {

    public NetworkMap {
        stations = List.copyOf(stations);
        lines = List.copyOf(lines);
    }
}
