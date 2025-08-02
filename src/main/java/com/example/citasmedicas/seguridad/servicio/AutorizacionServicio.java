package com.example.citasmedicas.seguridad.servicio;

import com.example.citasmedicas.repositorio.CitaRepositorio;
import com.example.citasmedicas.repositorio.DisponibilidadRepositorio;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio para comprobaciones de autorización a nivel de método.
 * Utilizado por @PreAuthorize en los controladores.
 */
@Service("autorizacionServicio") // Le damos un nombre al bean para referenciarlo en SpEL
public class AutorizacionServicio {

    private final CitaRepositorio citaRepositorio;
    private final DisponibilidadRepositorio disponibilidadRepositorio;

    public AutorizacionServicio(CitaRepositorio citaRepositorio, DisponibilidadRepositorio disponibilidadRepositorio) {
        this.citaRepositorio = citaRepositorio;
        this.disponibilidadRepositorio = disponibilidadRepositorio;
    }

    /**
     * Comprueba si el usuario autenticado es el propietario de una cita (ya sea paciente o doctor).
     */
    public boolean esPropietarioDeCita(Authentication authentication, UUID citaId) {
        return citaRepositorio.findById(citaId)
                .map(cita -> {
                    String username = authentication.getName();
                    return cita.getPaciente().getEmail().equals(username) ||
                           cita.getDoctor().getEmail().equals(username); // Asumiendo que el doctor tiene un email
                })
                .orElse(false); // Si la cita no existe, denegar acceso
    }

    /**
     * Comprueba si el doctor autenticado es el propietario de una disponibilidad.
     */
    public boolean esPropietarioDeDisponibilidad(Authentication authentication, UUID disponibilidadId) {
        return disponibilidadRepositorio.findById(disponibilidadId)
                .map(disponibilidad -> disponibilidad.getDoctor().getEmail().equals(authentication.getName()))
                .orElse(false);
    }

    // Podrías añadir más métodos como esPropietarioDePaciente, esPropietarioDeDoctor, etc.
    public boolean esPropietario(Authentication authentication, String username) {
        return authentication.getName().equals(username);
    }
}