package com.example.citasmedicas.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para solicitudes inválidas (HTTP 400 Bad Request).
 */
public class SolicitudInvalidaExcepcion extends RuntimeException {
    public SolicitudInvalidaExcepcion(String mensaje) {
        super(mensaje);
    }
}

