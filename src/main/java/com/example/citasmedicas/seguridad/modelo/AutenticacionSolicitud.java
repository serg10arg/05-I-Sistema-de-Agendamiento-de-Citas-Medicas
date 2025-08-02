package com.example.citasmedicas.seguridad.modelo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de autenticación (login).
 */
@Data
@Builder // Facilita la construcción de objetos [No en fuentes, práctica común]
@AllArgsConstructor
@NoArgsConstructor
public class AutenticacionSolicitud {
    @NotBlank(message = "El email no puede estar vacío.")
    private String email; // Nombre de usuario (email)

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String contrasena; // Contraseña
}

