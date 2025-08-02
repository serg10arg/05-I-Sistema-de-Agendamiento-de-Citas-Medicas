package com.example.citasmedicas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para la entidad Disponibilidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadDTO {
    private UUID id;
    private UUID doctorId; // ID del doctor
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime horaInicio; // Hora de inicio del bloque
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime horaFin; // Hora de fin del bloque
    private Boolean estaReservado; // Si el bloque ya está reservado
}

