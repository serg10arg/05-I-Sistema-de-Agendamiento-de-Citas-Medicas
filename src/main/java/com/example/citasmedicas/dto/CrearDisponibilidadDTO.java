package com.example.citasmedicas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Usar LocalDateTime
import java.util.UUID;

/**
 * DTO para crear un nuevo bloque de disponibilidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearDisponibilidadDTO {
    @NotNull(message = "El ID del doctor no puede ser nulo.")
    private UUID doctorId; // ID del doctor para quien se crea la disponibilidad

    @NotNull(message = "La hora de inicio no puede ser nula.")
    @FutureOrPresent(message = "La hora de inicio debe ser en el presente o futuro.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") // Formato ISO 8601 [No en fuentes, práctica común]
    private LocalDateTime horaInicio; // Hora de inicio del bloque

    @NotNull(message = "La hora de fin no puede ser nula.")
    @Future(message = "La hora de fin debe ser en el futuro.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime horaFin; // Hora de fin del bloque
}

