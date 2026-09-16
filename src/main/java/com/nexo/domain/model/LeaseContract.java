package com.nexo.domain.model;

import com.nexo.domain.event.LeaseStatusChangedEvent;
import com.nexo.domain.exception.InvalidLeaseTransitionException;
import com.nexo.domain.model.enums.InterestType;
import com.nexo.domain.model.enums.LeaseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaseContract {

    private final UUID id;
    private final UUID propertyId;
    private final UUID tenantId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal monthlyRent;
    private final BigDecimal dailyInterestRate;
    private final InterestType interestType;
    private LeaseStatus status;
    private final List<LeaseStatusChangedEvent> domainEvents = new ArrayList<>();

    public LeaseContract(
            UUID id,
            UUID propertyId,
            UUID tenantId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal monthlyRent,
            BigDecimal dailyInterestRate,
            InterestType interestType,
            LeaseStatus status) {
        this.id = id;
        this.propertyId = propertyId;
        this.tenantId = tenantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.dailyInterestRate = dailyInterestRate;
        this.interestType = interestType;
        this.status = status;
    }

    public void changeStatus(LeaseStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidLeaseTransitionException(status, newStatus);
        }
        domainEvents.add(new LeaseStatusChangedEvent(id, status, newStatus));
        status = newStatus;
    }

    public List<LeaseStatusChangedEvent> pullDomainEvents() {
        List<LeaseStatusChangedEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public BigDecimal getDailyInterestRate() {
        return dailyInterestRate;
    }

    public InterestType getInterestType() {
        return interestType;
    }

    public LeaseStatus getStatus() {
        return status;
    }
}
