package com.nexo.domain.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LeaseStatusTest {

    @ParameterizedTest
    @CsvSource({
        "DRAFT, ACTIVE, true",
        "DRAFT, TERMINATED, false",
        "ACTIVE, OVERDUE, true",
        "ACTIVE, TERMINATED, true",
        "ACTIVE, RENEWAL, true",
        "ACTIVE, DRAFT, false",
        "OVERDUE, ACTIVE, true",
        "OVERDUE, TERMINATED, true",
        "OVERDUE, RENEWAL, false",
        "RENEWAL, ACTIVE, true",
        "RENEWAL, TERMINATED, false",
        "TERMINATED, ACTIVE, false",
        "TERMINATED, DRAFT, false"
    })
    void evaluatesAllowedTransitions(LeaseStatus current, LeaseStatus target, boolean expected) {
        assertThat(current.canTransitionTo(target)).isEqualTo(expected);
    }
}
