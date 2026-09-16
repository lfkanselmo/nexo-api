package com.nexo.application.dto;

import com.nexo.domain.model.Property;
import java.util.UUID;

public record PropertyResponse(UUID id, String address, String city) {

    public static PropertyResponse from(Property property) {
        return new PropertyResponse(property.getId(), property.getAddress(), property.getCity());
    }
}
