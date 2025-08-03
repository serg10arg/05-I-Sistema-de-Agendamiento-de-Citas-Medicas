package com.example.citasmedicas.seguridad.modelo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de registro de un nuevo paciente.
 * Incluye la contraseña para la creación del usuario.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroPacienteSolicitud {

    @NotBlank(message = "El primer nombre no puede estar vacío.")
    @Size(max = 100)
    private String primerNombre;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El email debe tener un formato válido.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String contrasena;

    @Size(max = 20)
    private String telefono;
}