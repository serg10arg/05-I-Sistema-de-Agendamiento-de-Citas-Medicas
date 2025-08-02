package com.example.citasmedicas.dto;

import com.example.citasmedicas.modelo.entidad.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para actualizar el estado de una cita.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoCitaDTO {
    @NotNull(message = "El nuevo estado de la cita no puede ser nulo.")
    private EstadoCita nuevoEstado; // Nuevo estado de la cita
}
