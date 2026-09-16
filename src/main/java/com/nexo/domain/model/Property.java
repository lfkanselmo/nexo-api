package com.nexo.domain.model;

import java.util.UUID;

public class Property {

    private final UUID id;
    private String address;
    private String city;

    public Property(UUID id, String address, String city) {
        this.id = id;
        this.address = address;
        this.city = city;
    }

    public UUID getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
