package com.nexo.infrastructure.rest;

import com.nexo.application.dto.ContractRequest;
import com.nexo.application.dto.ContractResponse;
import com.nexo.application.dto.PenaltyResponse;
import com.nexo.application.service.ContractLifecycleService;
import com.nexo.application.service.PenaltyCalculator;
import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.port.ContractRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
class ContractController {

    private final ContractLifecycleService lifecycleService;
    private final ContractRepository contractRepository;
    private final PenaltyCalculator penaltyCalculator;

    ContractController(ContractLifecycleService lifecycleService, ContractRepository contractRepository, PenaltyCalculator penaltyCalculator) {
        this.lifecycleService = lifecycleService;
        this.contractRepository = contractRepository;
        this.penaltyCalculator = penaltyCalculator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ContractResponse create(@Valid @RequestBody ContractRequest request) {
        return ContractResponse.from(lifecycleService.create(request));
    }

    @GetMapping("/{id}")
    ContractResponse findById(@PathVariable UUID id) {
        return ContractResponse.from(lifecycleService.findById(id));
    }

    @GetMapping
    List<ContractResponse> findAll(@RequestParam(required = false) LeaseStatus status) {
        List<LeaseContract> contracts = status != null ? contractRepository.findByStatus(status) : contractRepository.findAll();
        return contracts.stream().map(ContractResponse::from).toList();
    }

    @PostMapping("/{id}/activate")
    ContractResponse activate(@PathVariable UUID id) {
        return ContractResponse.from(lifecycleService.activate(id));
    }

    @PostMapping("/{id}/terminate")
    ContractResponse terminate(@PathVariable UUID id) {
        return ContractResponse.from(lifecycleService.terminate(id));
    }

    @PostMapping("/{id}/renew")
    ContractResponse renew(@PathVariable UUID id) {
        return ContractResponse.from(lifecycleService.renew(id));
    }

    @GetMapping("/{id}/penalty")
    PenaltyResponse penalty(@PathVariable UUID id, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        return penaltyCalculator.calculate(lifecycleService.findById(id), asOf != null ? asOf : LocalDate.now());
    }
}
