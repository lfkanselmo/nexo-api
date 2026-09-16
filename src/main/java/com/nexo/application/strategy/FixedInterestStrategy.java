package com.nexo.application.strategy;

import com.nexo.domain.model.enums.InterestType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class FixedInterestStrategy implements InterestStrategy {

    @Override
    public InterestType type() {
        return InterestType.FIXED;
    }

    @Override
    public BigDecimal calculate(BigDecimal principal, BigDecimal dailyRate, long daysLate) {
        return principal.multiply(dailyRate).multiply(BigDecimal.valueOf(daysLate)).setScale(2, RoundingMode.HALF_UP);
    }
}
