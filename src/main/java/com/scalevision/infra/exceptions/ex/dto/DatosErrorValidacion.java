package com.scalevision.infra.exceptions.ex.dto;

public record DatosErrorValidacion(String campo, String error) {
    public DatosErrorValidacion(org.springframework.validation.FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
