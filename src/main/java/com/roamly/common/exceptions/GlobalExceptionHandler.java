package com.roamly.common.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<String> handleModelNotFoundException(ModelNotFoundException ex) {
        return status(NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(KeycloakClientUnreachableException.class)
    public ResponseEntity<String> handleKeycloakClientUnreachableException(KeycloakClientUnreachableException ex) {
        return status(UNAUTHORIZED).body(ex.getMessage());
    }
}