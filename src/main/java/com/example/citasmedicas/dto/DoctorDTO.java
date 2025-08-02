package com.example.citasmedicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para la entidad Doctor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private UUID id;
    @NotBlank(message = "El primer nombre del doctor no puede estar vacío.")
    @Size(max = 100, message = "El primer nombre no puede exceder los 100 caracteres.")
    private String primerNombre; // Primer nombre del doctor

    @NotBlank(message = "El apellido del doctor no puede estar vacío.")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres.")
    private String apellido; // Apellido del doctor

    @NotBlank(message = "El email del doctor no puede estar vacío.")
    @Email(message = "El email debe tener un formato válido.")
    @Size(max = 150, message = "El email no puede exceder los 150 caracteres.")
    private String email; // Correo electrónico del doctor

    @Size(max = 255, message = "La URL de la foto de perfil no puede exceder los 255 caracteres.")
    private String urlFotoPerfil; // URL de la foto de perfil

    @NotNull(message = "La especialidad no puede ser nula.")
    private EspecialidadDTO especialidad; // Especialidad del doctor

    private String biografia; // Biografía del doctor
}
