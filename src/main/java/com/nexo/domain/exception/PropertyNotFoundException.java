package com.nexo.domain.exception;

import java.util.UUID;

public class PropertyNotFoundException extends DomainException {

    public PropertyNotFoundException(UUID id) {
        super("No existe la propiedad %s".formatted(id));
    }
}
