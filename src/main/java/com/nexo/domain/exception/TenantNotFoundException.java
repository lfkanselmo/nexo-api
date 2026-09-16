package com.nexo.domain.exception;

import java.util.UUID;

public class TenantNotFoundException extends DomainException {

    public TenantNotFoundException(UUID id) {
        super("No existe el inquilino %s".formatted(id));
    }
}
