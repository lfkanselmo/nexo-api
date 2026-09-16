package com.nexo.infrastructure.persistence;

import com.nexo.domain.model.enums.InterestType;
import com.nexo.domain.model.enums.LeaseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lease_contracts")
public class LeaseContractEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID propertyId;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal monthlyRent;

    @Column(nullable = false)
    private BigDecimal dailyInterestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestType interestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaseStatus status;

    protected LeaseContractEntity() {
    }

    public LeaseContractEntity(
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

    public void setStatus(LeaseStatus status) {
        this.status = status;
    }
}
