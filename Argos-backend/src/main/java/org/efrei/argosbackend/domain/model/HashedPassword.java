package org.efrei.argosbackend.domain.model;
public record HashedPassword(String value) {


    @Override
    public String toString(){
        return "*****";
    }
}
