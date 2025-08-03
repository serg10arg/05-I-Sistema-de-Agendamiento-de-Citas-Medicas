package com.example.citasmedicas.seguridad.servicio;

import com.example.citasmedicas.repositorio.CitaRepositorio;
import com.example.citasmedicas.repositorio.DisponibilidadRepositorio;
import com.example.citasmedicas.repositorio.DoctorRepositorio;
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
    private final DoctorRepositorio doctorRepositorio;

    public AutorizacionServicio(CitaRepositorio citaRepositorio, DisponibilidadRepositorio disponibilidadRepositorio, DoctorRepositorio doctorRepositorio) {
        this.citaRepositorio = citaRepositorio;
        this.disponibilidadRepositorio = disponibilidadRepositorio;
        this.doctorRepositorio = doctorRepositorio;
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

    /**
     * Comprueba si el usuario autenticado es el doctor correspondiente a un ID.
     * @param authentication El objeto de autenticación actual.
     * @param doctorId El UUID del doctor a comprobar.
     * @return true si el email del usuario autenticado coincide con el email del doctor.
     */
    public boolean esPropietarioDeDoctor(Authentication authentication, UUID doctorId) {
        return doctorRepositorio.findById(doctorId)
                .map(doctor -> doctor.getEmail().equals(authentication.getName()))
                .orElse(false); // Si el doctor no existe, denegar acceso
    }

    // Este método es demasiado genérico y propenso a errores, se podría eliminar
    // public boolean esPropietario(Authentication authentication, String username) { ... }
}