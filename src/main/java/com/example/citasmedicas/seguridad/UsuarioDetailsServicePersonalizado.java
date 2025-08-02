package com.example.citasmedicas.seguridad;

import com.example.citasmedicas.seguridad.enumeracion.RolUsuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList; // No en fuentes, pero necesario para List

/**
 * Implementación personalizada de UserDetailsService.
 * Para este ejemplo, simula usuarios en memoria.
 * En un sistema real, se cargarían usuarios de una base de datos.
 */
@Service
public class UsuarioDetailsServicePersonalizado implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    // Simulación de usuarios en memoria. En un sistema real, se usaría un repositorio.
    private final List<UserDetails> usuarios;

    public UsuarioDetailsServicePersonalizado(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        // Inicializar la lista aquí, cuando passwordEncoder ya no es nulo.
        this.usuarios = new ArrayList<>(Arrays.asList(
                User.builder()
                        .username("paciente1@example.com")
                        .password(this.passwordEncoder.encode("password")) // Contraseña codificada
                        .roles(RolUsuario.PATIENT.name()) // Rol PATIENT
                        .build(),
                User.builder()
                        .username("doctor1@example.com")
                        .password(this.passwordEncoder.encode("password"))
                        .roles(RolUsuario.DOCTOR.name()) // Rol DOCTOR
                        .build(),
                User.builder()
                        .username("admin@example.com")
                        .password(this.passwordEncoder.encode("password"))
                        .roles(RolUsuario.ADMIN.name()) // Rol ADMIN
                        .build()
        ));
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario (email en este caso).
     * @param username El nombre de usuario (email).
     * @return UserDetails con la información del usuario.
     * @throws UsernameNotFoundException Si el usuario no es encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarios.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
