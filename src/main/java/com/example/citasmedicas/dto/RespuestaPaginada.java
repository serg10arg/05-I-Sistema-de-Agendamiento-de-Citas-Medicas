package com.example.citasmedicas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO genérico para encapsular respuestas paginadas de la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaPaginada<T> {
    private List<T> contenido; // La lista de elementos de la página actual
    private PaginacionMetadata metadatos; // Los metadatos de paginación
}