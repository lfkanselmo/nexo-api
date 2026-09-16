package com.nexo.domain.exception;

import java.util.UUID;

public class ContractNotFoundException extends DomainException {

    public ContractNotFoundException(UUID id) {
        super("No existe el contrato %s".formatted(id));
    }
}
