package com.example.citasmedicas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuración para la auditoría de JPA.
 * Define cómo obtener el "auditor" (usuario) actual para los campos de auditoría.
 */
@Configuration
public class AuditoriaConfig implements AuditorAware<String> {

    /**
     * Retorna el usuario actual del contexto de seguridad.
     * Esto permite a Spring Data JPA registrar automáticamente quién creó o modificó una entidad.
     * @return Un Optional que contiene el nombre del usuario actual.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // Obtiene la autenticación del contexto de seguridad de Spring.
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay autenticación, o es anónima, o no está autenticada, no hay auditor.
        if (autenticacion == null || !autenticacion.isAuthenticated() || "anonymousUser".equals(autenticacion.getPrincipal())) {
            return Optional.empty();
        }

        // Retorna el nombre del principal autenticado como el auditor.
        return Optional.of(autenticacion.getName());
    }
}

