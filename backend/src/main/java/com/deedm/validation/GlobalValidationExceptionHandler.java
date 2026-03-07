package com.deedm.validation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.deedm.controller")
public class GlobalValidationExceptionHandler {

    @ExceptionHandler(RequestPayloadValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePayloadValidation(RequestPayloadValidationException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("exceeds limit")
            ? HttpStatus.PAYLOAD_TOO_LARGE
            : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of(
            "success", false,
            "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "success", false,
            "error", "Request body format is invalid"
        ));
    }
}
