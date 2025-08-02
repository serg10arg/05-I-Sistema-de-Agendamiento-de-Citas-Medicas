package com.example.citasmedicas.seguridad.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación, que contiene el token JWT.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutenticacionRespuesta {
    private String token; // Token JWT
}

