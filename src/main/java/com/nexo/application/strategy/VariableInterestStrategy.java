package com.nexo.application.strategy;

import com.nexo.domain.model.enums.InterestType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class VariableInterestStrategy implements InterestStrategy {

    @Override
    public InterestType type() {
        return InterestType.VARIABLE;
    }

    @Override
    public BigDecimal calculate(BigDecimal principal, BigDecimal dailyRate, long daysLate) {
        BigDecimal growthFactor = BigDecimal.ONE.add(dailyRate).pow((int) daysLate, MathContext.DECIMAL64);
        return principal.multiply(growthFactor.subtract(BigDecimal.ONE)).setScale(2, RoundingMode.HALF_UP);
    }
}
