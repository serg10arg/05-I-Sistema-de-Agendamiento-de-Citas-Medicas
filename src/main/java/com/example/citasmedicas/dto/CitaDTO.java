package com.example.citasmedicas.dto;

import com.example.citasmedicas.modelo.entidad.EstadoCita;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para la entidad Cita, usado como respuesta de la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {
    private UUID id; // ID de la cita
    private UUID doctorId; // ID del doctor
    private UUID pacienteId; // ID del paciente
    private UUID disponibilidadId; // ID del bloque de disponibilidad

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC") // Formato ISO 8601 con Z para UTC
    private LocalDateTime horaInicio; // Hora de inicio de la cita

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime horaFin; // Hora de fin de la cita

    private EstadoCita estado; // Estado de la cita (CONFIRMADA, CANCELADA, FINALIZADA)
    private String razonVisita; // Razón de la visita

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant fechaCreacion; // Fecha de creación de la cita
}
