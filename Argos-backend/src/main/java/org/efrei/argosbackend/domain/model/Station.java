package org.efrei.argosbackend.domain.model;

import java.io.Serializable;
import java.util.UUID;

public record Station(
        UUID id,
        String name,
        String coordinates
){

}
