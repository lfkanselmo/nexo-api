package com.nexo.domain.model.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum LeaseStatus {
    DRAFT,
    ACTIVE,
    OVERDUE,
    TERMINATED,
    RENEWAL;

    private static final Map<LeaseStatus, Set<LeaseStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(LeaseStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(DRAFT, EnumSet.of(ACTIVE));
        ALLOWED_TRANSITIONS.put(ACTIVE, EnumSet.of(OVERDUE, TERMINATED, RENEWAL));
        ALLOWED_TRANSITIONS.put(OVERDUE, EnumSet.of(ACTIVE, TERMINATED));
        ALLOWED_TRANSITIONS.put(RENEWAL, EnumSet.of(ACTIVE));
        ALLOWED_TRANSITIONS.put(TERMINATED, EnumSet.noneOf(LeaseStatus.class));
    }

    public boolean canTransitionTo(LeaseStatus target) {
        return ALLOWED_TRANSITIONS.get(this).contains(target);
    }
}
