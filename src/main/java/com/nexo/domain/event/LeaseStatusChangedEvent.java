package com.nexo.domain.event;

import com.nexo.domain.model.enums.LeaseStatus;
import java.time.Instant;
import java.util.UUID;

public record LeaseStatusChangedEvent(UUID contractId, LeaseStatus previousStatus, LeaseStatus newStatus, Instant occurredOn) {

    public LeaseStatusChangedEvent(UUID contractId, LeaseStatus previousStatus, LeaseStatus newStatus) {
        this(contractId, previousStatus, newStatus, Instant.now());
    }
}
