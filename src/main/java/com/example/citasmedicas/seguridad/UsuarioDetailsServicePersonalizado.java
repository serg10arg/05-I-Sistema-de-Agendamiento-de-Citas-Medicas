package com.example.citasmedicas.seguridad;

import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Paciente;
import com.example.citasmedicas.repositorio.DoctorRepositorio;
import com.example.citasmedicas.repositorio.PacienteRepositorio;
import com.example.citasmedicas.seguridad.enumeracion.RolUsuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementación personalizada de UserDetailsService.
 * Carga los detalles del usuario desde la base de datos.
 */
@Service
public class UsuarioDetailsServicePersonalizado implements UserDetailsService {

    private final PacienteRepositorio pacienteRepositorio;
    private final DoctorRepositorio doctorRepositorio;

    public UsuarioDetailsServicePersonalizado(PacienteRepositorio pacienteRepositorio, DoctorRepositorio doctorRepositorio) {
        this.pacienteRepositorio = pacienteRepositorio;
        this.doctorRepositorio = doctorRepositorio;
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario (email en este caso).
     * @param username El nombre de usuario (email).
     * @return UserDetails con la información del usuario.
     * @throws UsernameNotFoundException Si el usuario no es encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar primero en el repositorio de pacientes
        return pacienteRepositorio.findByEmail(username)
                .map(this::crearUserDetailsDesdePaciente)
                .orElseGet(() -> doctorRepositorio.findByEmail(username) // Si no se encuentra, buscar en doctores
                        .map(this::crearUserDetailsDesdeDoctor)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username)));
    }

    private UserDetails crearUserDetailsDesdePaciente(Paciente paciente) {
        return User.withUsername(paciente.getEmail())
                .password(paciente.getContrasena())
                .roles(RolUsuario.PATIENT.name())
                .build();
    }

    private UserDetails crearUserDetailsDesdeDoctor(Doctor doctor) {
        return User.withUsername(doctor.getEmail())
                .password(doctor.getContrasena())
                .roles(RolUsuario.DOCTOR.name()) // Asumimos que todos los doctores tienen este rol
                .build();
    }
}
