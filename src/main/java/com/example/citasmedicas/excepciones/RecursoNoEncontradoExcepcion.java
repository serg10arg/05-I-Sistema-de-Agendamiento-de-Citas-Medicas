package com.example.citasmedicas.excepciones;

/**
 * Excepción personalizada para recursos no encontrados (HTTP 404).
 */
public class RecursoNoEncontradoExcepcion extends RuntimeException {
  public RecursoNoEncontradoExcepcion(String mensaje) {
    super(mensaje);
  }
}
