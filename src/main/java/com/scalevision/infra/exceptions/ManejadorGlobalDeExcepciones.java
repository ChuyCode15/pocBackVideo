package com.scalevision.infra.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.scalevision.infra.exceptions.ex.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorGlobalDeExcepciones {

    // --- 404: NO ENCONTRADO (ESPECÍFICOS) ---

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        return crearRespuesta(HttpStatus.NOT_FOUND, "Entidad no encontrada", "El registro solicitado no existe en la base de datos.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return crearRespuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
        return crearRespuesta(HttpStatus.NOT_FOUND, "Ruta no encontrada", "❌ No existe un endpoint para: " + ex.getRequestURL());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        return crearRespuesta(HttpStatus.NOT_FOUND, "Recurso estático no encontrado", "⚠️ No se halló el archivo: " + ex.getResourcePath());
    }

    // --- 400: PETICIÓN INCORRECTA / VALIDACIÓN ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(err -> err.getField(), err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Error", (e, n) -> e));

        Map<String, Object> body = estructuraBase(HttpStatus.BAD_REQUEST, "Validación fallida");
        body.put("campos", errores);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String mensaje = "Error al procesar el JSON. Verifica la sintaxis.";
        if (cause instanceof InvalidFormatException ife) {
            mensaje = String.format("Valor '%s' inválido para el campo '%s'.", ife.getValue(), ife.getPath().get(0).getFieldName());
        } else if (ex.getMessage().contains("Required request body is missing")) {
            mensaje = "El cuerpo de la solicitud es obligatorio.";
        }
        return crearRespuesta(HttpStatus.BAD_REQUEST, "JSON Malformado", mensaje);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, "Tipo de dato incorrecto", "El parámetro '" + ex.getName() + "' debe ser " + ex.getRequiredType().getSimpleName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage());
    }

    // --- 409: CONFLICTOS ---

    @ExceptionHandler({DuplicateResourceException.class, RecursoExistenteException.class, EntityExistsException.class, RuleValidationException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(Exception ex) {
        return crearRespuesta(HttpStatus.CONFLICT, "Conflicto", ex.getMessage());
    }

    // --- 405: MÉTODO NO SOPORTADO ---

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return crearRespuesta(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido", "El método " + ex.getMethod() + " no está permitido aquí.");
    }

    // --- 500/503: ERRORES INTERNOS ---
/*

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return crearRespuesta(HttpStatus.SERVICE_UNAVAILABLE, "Servicio no disponible", "Error en el procesamiento: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return crearRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Ocurrió un error inesperado en el servidor.");
    }
*/

    // --- UTILITARIOS ---

    private Map<String, Object> estructuraBase(HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        return body;
    }

    private ResponseEntity<Map<String, Object>> crearRespuesta(HttpStatus status, String error, String mensaje) {
        Map<String, Object> body = estructuraBase(status, error);
        body.put("message", mensaje);
        return new ResponseEntity<>(body, status);
    }
}