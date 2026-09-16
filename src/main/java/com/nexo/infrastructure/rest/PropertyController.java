package com.nexo.infrastructure.rest;

import com.nexo.application.dto.PropertyRequest;
import com.nexo.application.dto.PropertyResponse;
import com.nexo.domain.exception.PropertyNotFoundException;
import com.nexo.domain.model.Property;
import com.nexo.domain.port.PropertyRepository;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
class PropertyController {

    private final PropertyRepository propertyRepository;

    PropertyController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PropertyResponse create(@Valid @RequestBody PropertyRequest request) {
        Property property = new Property(UUID.randomUUID(), request.address(), request.city());
        return PropertyResponse.from(propertyRepository.save(property));
    }

    @GetMapping("/{id}")
    ResponseEntity<PropertyResponse> findById(@PathVariable UUID id) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new PropertyNotFoundException(id));
        return ResponseEntity.ok(PropertyResponse.from(property));
    }
}
