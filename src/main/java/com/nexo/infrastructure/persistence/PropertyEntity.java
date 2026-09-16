package com.nexo.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "properties")
public class PropertyEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    protected PropertyEntity() {
    }

    public PropertyEntity(UUID id, String address, String city) {
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
