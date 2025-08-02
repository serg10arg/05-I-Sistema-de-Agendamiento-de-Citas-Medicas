package com.example.citasmedicas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para incluir metadatos de paginación en las respuestas de lista.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginacionMetadata {
    private long totalElementos; // Número total de elementos
    private int totalPaginas; // Número total de páginas
    private int paginaActual; // Página actual (basada en 0)
    private int tamanoPagina; // Tamaño de la página (limit)
}

