package com.scalevision.infra.exceptions.ex;

public class DatosErrorValidacion extends RuntimeException {
    public DatosErrorValidacion(String mensaje) {
        super(mensaje);
    }
}