package com.example.citasmedicas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO estandarizado para representar respuestas de error de la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaError {
    private String codigo; // Código de error específico de la aplicación
    private String mensaje; // Mensaje de error legible por el usuario/desarrollador
    private Map<String, String> detalles; // Detalles de error por campo (para errores de validación)
    private Instant marcaDeTiempo; // Marca de tiempo de cuándo ocurrió el error
    private int estadoHttp; // Código de estado HTTP
}
