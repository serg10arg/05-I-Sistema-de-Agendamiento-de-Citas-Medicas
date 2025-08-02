package com.example.citasmedicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para la entidad Especialidad.
 */
@Data // Genera getters, setters, toString, equals y hashCode [No en fuentes, práctica común]
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadDTO {
    private UUID id;
    @NotBlank(message = "El nombre de la especialidad no puede estar vacío.")
    @Size(max = 100, message = "El nombre de la especialidad no puede exceder los 100 caracteres.")
    private String nombre; // Nombre de la especialidad
}

