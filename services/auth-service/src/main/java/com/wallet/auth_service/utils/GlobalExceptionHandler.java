package com.wallet.auth_service.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Map messages to HTTP status codes
    private static final Map<String, HttpStatus> STATUS_CODES = Map.of(
        "Email already in use", HttpStatus.CONFLICT,
        "Phone already exists", HttpStatus.CONFLICT,
        "User not found", HttpStatus.NOT_FOUND,
        "Invalid credentials", HttpStatus.UNAUTHORIZED
    );

    // Catch all IllegalArgumentExceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();
        HttpStatus status = STATUS_CODES.getOrDefault(message, HttpStatus.BAD_REQUEST);

        // Return JSON with message and status
        return ResponseEntity.status(status).body(Map.of(
            "status", status.value(),
            "message", message
        ));
    }

    // Catch all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        ex.printStackTrace(); // log for debugging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "status", 500,
            "message", "Server error. Please try again later."
        ));
    }
}
