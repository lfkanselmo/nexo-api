package com.nexo.infrastructure.rest;

import com.nexo.application.dto.ContractRequest;
import com.nexo.application.dto.ContractResponse;
import com.nexo.application.service.ContractLifecycleService;
import com.nexo.domain.model.enums.LeaseStatus;
import com.nexo.domain.port.ContractRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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

    ContractController(ContractLifecycleService lifecycleService, ContractRepository contractRepository) {
        this.lifecycleService = lifecycleService;
        this.contractRepository = contractRepository;
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
    List<ContractResponse> findByStatus(@RequestParam LeaseStatus status) {
        return contractRepository.findByStatus(status).stream().map(ContractResponse::from).toList();
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
}
