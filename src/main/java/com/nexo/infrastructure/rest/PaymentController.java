package com.nexo.infrastructure.rest;

import com.nexo.application.dto.PaymentRequest;
import com.nexo.application.dto.PaymentResponse;
import com.nexo.domain.exception.ContractNotFoundException;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.enums.PaymentStatus;
import com.nexo.domain.port.ContractRepository;
import com.nexo.domain.port.PaymentRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts/{contractId}/payments")
class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;

    PaymentController(PaymentRepository paymentRepository, ContractRepository contractRepository) {
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PaymentResponse schedule(@PathVariable UUID contractId, @Valid @RequestBody PaymentRequest request) {
        contractRepository.findById(contractId).orElseThrow(() -> new ContractNotFoundException(contractId));
        Payment payment = new Payment(UUID.randomUUID(), contractId, request.dueDate(), request.amount(), PaymentStatus.PENDING);
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @GetMapping
    List<PaymentResponse> list(@PathVariable UUID contractId) {
        return paymentRepository.findByContractId(contractId).stream().map(PaymentResponse::from).toList();
    }
}
