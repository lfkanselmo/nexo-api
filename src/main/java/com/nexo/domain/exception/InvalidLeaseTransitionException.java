package com.nexo.domain.exception;

import com.nexo.domain.model.enums.LeaseStatus;

public class InvalidLeaseTransitionException extends DomainException {

    public InvalidLeaseTransitionException(LeaseStatus current, LeaseStatus target) {
        super("No se puede pasar de %s a %s".formatted(current, target));
    }
}
