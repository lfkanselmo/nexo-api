package com.nexo.infrastructure.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexo.application.service.ContractLifecycleService;
import com.nexo.application.service.PaymentReminderService;
import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.enums.InterestType;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.model.enums.PaymentStatus;
import com.nexo.domain.port.ContractRepository;
import com.nexo.domain.port.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverdueDetectorTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ContractLifecycleService lifecycleService;

    @Mock
    private PaymentReminderService reminderService;

    private OverdueDetector detector;

    @BeforeEach
    void setUp() {
        detector = new OverdueDetector(contractRepository, paymentRepository, lifecycleService, reminderService);
    }

    private LeaseContract activeContract(UUID id) {
        return new LeaseContract(
                id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusYears(1), new BigDecimal("1000"), new BigDecimal("0.01"), InterestType.FIXED, LeaseStatus.ACTIVE);
    }

    @Test
    void marksAnActiveContractOverdueWhenItHasAnOverduePayment() {
        UUID contractId = UUID.randomUUID();
        Payment overduePayment = new Payment(UUID.randomUUID(), contractId, LocalDate.now().minusDays(3), new BigDecimal("1000"), PaymentStatus.PENDING);

        when(paymentRepository.findPendingDueBefore(any())).thenReturn(List.of(overduePayment));
        when(contractRepository.findByStatus(LeaseStatus.ACTIVE)).thenReturn(List.of(activeContract(contractId)));

        detector.run();

        verify(lifecycleService).markOverdue(contractId);
        verify(reminderService).sendDueReminders(any());
    }

    @Test
    void leavesAnActiveContractAloneWhenItHasNoOverduePayment() {
        UUID contractId = UUID.randomUUID();

        when(paymentRepository.findPendingDueBefore(any())).thenReturn(List.of());
        when(contractRepository.findByStatus(LeaseStatus.ACTIVE)).thenReturn(List.of(activeContract(contractId)));

        detector.run();

        verify(lifecycleService, never()).markOverdue(any());
    }
}
