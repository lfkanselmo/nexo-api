package com.nexo.application.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexo.domain.model.enums.InterestType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FixedInterestStrategyTest {

    private final FixedInterestStrategy strategy = new FixedInterestStrategy();

    @Test
    void reportsFixedAsItsType() {
        assertThat(strategy.type()).isEqualTo(InterestType.FIXED);
    }

    @Test
    void calculatesSimpleInterestLinearlyOverDays() {
        BigDecimal interest = strategy.calculate(new BigDecimal("1200.00"), new BigDecimal("0.01"), 10);

        assertThat(interest).isEqualByComparingTo("120.00");
    }

    @Test
    void returnsZeroWhenThereAreNoDaysLate() {
        BigDecimal interest = strategy.calculate(new BigDecimal("1200.00"), new BigDecimal("0.01"), 0);

        assertThat(interest).isEqualByComparingTo("0.00");
    }
}
