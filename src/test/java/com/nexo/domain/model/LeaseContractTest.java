package com.nexo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexo.domain.event.LeaseStatusChangedEvent;
import com.nexo.domain.exception.InvalidLeaseTransitionException;
import com.nexo.domain.model.enums.InterestType;
import com.nexo.domain.model.enums.LeaseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LeaseContractTest {

    private LeaseContract newContract(LeaseStatus status) {
        return new LeaseContract(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("1200.00"),
                new BigDecimal("0.0015"),
                InterestType.FIXED,
                status);
    }

    @Test
    void changesStatusWhenTransitionIsAllowed() {
        LeaseContract contract = newContract(LeaseStatus.DRAFT);

        contract.changeStatus(LeaseStatus.ACTIVE);

        assertThat(contract.getStatus()).isEqualTo(LeaseStatus.ACTIVE);
    }

    @Test
    void rejectsTransitionWhenNotAllowed() {
        LeaseContract contract = newContract(LeaseStatus.DRAFT);

        assertThatThrownBy(() -> contract.changeStatus(LeaseStatus.TERMINATED))
                .isInstanceOf(InvalidLeaseTransitionException.class);
        assertThat(contract.getStatus()).isEqualTo(LeaseStatus.DRAFT);
    }

    @Test
    void recordsDomainEventOnSuccessfulTransition() {
        LeaseContract contract = newContract(LeaseStatus.ACTIVE);

        contract.changeStatus(LeaseStatus.OVERDUE);

        assertThat(contract.pullDomainEvents())
                .singleElement()
                .extracting(LeaseStatusChangedEvent::previousStatus, LeaseStatusChangedEvent::newStatus)
                .containsExactly(LeaseStatus.ACTIVE, LeaseStatus.OVERDUE);
    }

    @Test
    void clearsDomainEventsAfterPull() {
        LeaseContract contract = newContract(LeaseStatus.ACTIVE);
        contract.changeStatus(LeaseStatus.OVERDUE);

        contract.pullDomainEvents();

        assertThat(contract.pullDomainEvents()).isEmpty();
    }
}
