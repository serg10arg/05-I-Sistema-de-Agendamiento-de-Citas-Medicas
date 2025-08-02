package com.example.citasmedicas.excepciones;

/**
 * Excepción personalizada para conflictos de horario o disponibilidad (HTTP 409 Conflict).
 */
public class ConflictoHorarioExcepcion extends RuntimeException {
    public ConflictoHorarioExcepcion(String mensaje) {
        super(mensaje);
    }
}
