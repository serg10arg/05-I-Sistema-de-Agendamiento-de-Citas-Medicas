package com.example.citasmedicas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO para la entidad Paciente.
 */
public record PacienteDTO(
        UUID id,
        @NotBlank(message = "El primer nombre del paciente no puede estar vacío.")
        @Size(max = 100, message = "El primer nombre no puede exceder los 100 caracteres.")
        String primerNombre, // Primer nombre del paciente
        @NotBlank(message = "El apellido del paciente no puede estar vacío.")
        @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres.")
        String apellido, // Apellido del paciente
        @NotBlank(message = "El email del paciente no puede estar vacío.")
        @Email(message = "El email debe tener un formato válido.")
        @Size(max = 150, message = "El email no puede exceder los 150 caracteres.")
        String email, // Correo electrónico del paciente
        @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres.")
        String telefono // Número de teléfono del paciente
) {
}
