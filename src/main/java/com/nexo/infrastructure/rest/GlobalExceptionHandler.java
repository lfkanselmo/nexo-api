package com.nexo.infrastructure.rest;

import com.nexo.domain.exception.ContractNotFoundException;
import com.nexo.domain.exception.InvalidLeaseTransitionException;
import com.nexo.domain.exception.PropertyNotFoundException;
import com.nexo.domain.exception.TenantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler({ContractNotFoundException.class, PropertyNotFoundException.class, TenantNotFoundException.class})
    ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidLeaseTransitionException.class)
    ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidLeaseTransitionException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .reduce((a, b) -> a + "; " + b)
                .orElse("Solicitud inválida");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }
}
