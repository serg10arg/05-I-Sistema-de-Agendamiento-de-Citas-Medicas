package com.example.citasmedicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de entrada (payload) para crear una nueva cita.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearCitaDTO {
    @NotNull(message = "El ID del doctor no puede ser nulo.")
    private UUID doctorId; // ID del doctor para la cita

    @NotNull(message = "El ID del paciente no puede ser nulo.")
    private UUID pacienteId; // ID del paciente que agenda la cita

    @NotNull(message = "El ID de la disponibilidad no puede ser nulo.")
    private UUID disponibilidadId; // ID del bloque de disponibilidad seleccionado

    @Size(max = 500, message = "La razón de la visita no puede exceder los 500 caracteres.")
    private String razonVisita; // Razón de la visita
}
