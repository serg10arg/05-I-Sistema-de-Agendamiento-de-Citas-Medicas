package com.example.citasmedicas.excepciones;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para acceso denegado (HTTP 403 Forbidden).
 * Permite un código de error específico para el cliente.
 */
@Getter
public class AccesoDenegadoExcepcion extends RuntimeException {
    private final String codigoError; // Código de error específico de la aplicación
    private final String detalles; // Detalles adicionales

    public AccesoDenegadoExcepcion(String codigoError, String mensaje, String detalles) {
        super(mensaje);
        this.codigoError = codigoError;
        this.detalles = detalles;
    }
}

