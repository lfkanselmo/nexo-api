package com.nexo.application.strategy;

import com.nexo.domain.model.enums.InterestType;
import java.math.BigDecimal;

public interface InterestStrategy {

    InterestType type();

    BigDecimal calculate(BigDecimal principal, BigDecimal dailyRate, long daysLate);
}
