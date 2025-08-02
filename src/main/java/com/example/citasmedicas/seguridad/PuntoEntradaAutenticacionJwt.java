package com.example.citasmedicas.seguridad;

import com.example.citasmedicas.dto.RespuestaError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Punto de entrada de autenticación para JWT.
 * Se invoca cuando un usuario no autenticado intenta acceder a un recurso protegido.
 * Devuelve un error 401 Unauthorized.
 */
@Component
public class PuntoEntradaAutenticacionJwt implements AuthenticationEntryPoint {

    /**
     * Se invoca cuando una autenticación no autorizada es rechazada.
     * @param request La petición HTTP que causó la excepción.
     * @param response La respuesta HTTP.
     * @param authException La excepción de autenticación que fue lanzada.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // Devolver una respuesta JSON estandarizada en lugar de la página de error por defecto.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        RespuestaError error = new RespuestaError(
                "NO_AUTORIZADO",
                "Se requiere autenticación para acceder a este recurso.",
                Map.of("detalle", authException.getMessage()),
                Instant.now(),
                HttpServletResponse.SC_UNAUTHORIZED
        );

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
