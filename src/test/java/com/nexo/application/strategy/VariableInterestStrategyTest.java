package com.nexo.application.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexo.domain.model.enums.InterestType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class VariableInterestStrategyTest {

    private final VariableInterestStrategy strategy = new VariableInterestStrategy();

    @Test
    void reportsVariableAsItsType() {
        assertThat(strategy.type()).isEqualTo(InterestType.VARIABLE);
    }

    @Test
    void compoundsDailyInterestOverDaysLate() {
        BigDecimal interest = strategy.calculate(new BigDecimal("1000"), new BigDecimal("0.01"), 10);

        assertThat(interest).isEqualByComparingTo("104.62");
    }

    @Test
    void growsFasterThanFixedInterestForTheSamePrincipalAndDays() {
        BigDecimal principal = new BigDecimal("1000");
        BigDecimal dailyRate = new BigDecimal("0.01");

        BigDecimal variable = strategy.calculate(principal, dailyRate, 30);
        BigDecimal fixed = new FixedInterestStrategy().calculate(principal, dailyRate, 30);

        assertThat(variable).isGreaterThan(fixed);
    }
}
