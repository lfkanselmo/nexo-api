package com.nexo.infrastructure.mail;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.Tenant;
import com.nexo.domain.port.ReminderNotifier;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
class JavaMailReminderNotifier implements ReminderNotifier {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    JavaMailReminderNotifier(JavaMailSender mailSender, @Value("${nexo.mail.reminder-from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void remind(Tenant tenant, LeaseContract contract, Payment payment) {
        long daysLate = ChronoUnit.DAYS.between(payment.getDueDate(), LocalDate.now());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(tenant.getEmail());
        message.setSubject("Pago pendiente de tu contrato de arrendamiento");
        message.setText(
                """
                Hola %s,

                Tenés un pago de %s vencido desde el %s (%d días de atraso) para tu contrato %s.

                Por favor ponte al día lo antes posible para evitar que sigan corriendo los intereses de mora.

                — Nexo
                """
                        .formatted(tenant.getFullName(), payment.getAmount(), payment.getDueDate(), daysLate, contract.getId()));

        mailSender.send(message);
    }
}
