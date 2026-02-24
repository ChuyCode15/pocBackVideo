package com.scalevision.infra.exceptions;

import com.scalevision.infra.exceptions.ex.DuplicateResourceException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalDeExcepciones {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateException(Exception ex) {
        Map<String, Object>  error = new HashMap<>();
        error.put("status", 409);
        error.put("error", "CONFLICT");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
