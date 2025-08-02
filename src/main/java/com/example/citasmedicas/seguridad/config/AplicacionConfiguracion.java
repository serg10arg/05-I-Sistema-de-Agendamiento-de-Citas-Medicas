package com.example.citasmedicas.seguridad.config;

import com.example.citasmedicas.seguridad.UsuarioDetailsServicePersonalizado;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración para beans de seguridad relacionados con la autenticación.
 */
@Configuration
public class AplicacionConfiguracion {

    private final UsuarioDetailsServicePersonalizado usuarioDetailsServicePersonalizado;

    public AplicacionConfiguracion(UsuarioDetailsServicePersonalizado usuarioDetailsServicePersonalizado) {
        this.usuarioDetailsServicePersonalizado = usuarioDetailsServicePersonalizado;
    }

    /**
     * Define el proveedor de autenticación. Utiliza DaoAuthenticationProvider para autenticación basada en usuario/contraseña.
     * @return El AuthenticationProvider configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioDetailsServicePersonalizado); // Establece el servicio para cargar usuarios
        authProvider.setPasswordEncoder(passwordEncoder()); // Establece el codificador de contraseñas
        return authProvider;
    }

    /**
     * Define el codificador de contraseñas. Se recomienda BCrypt para un almacenamiento seguro.
     * @return El PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Codificador de contraseñas BCrypt
    }

    /**
     * Expone el AuthenticationManager del contexto de seguridad.
     * @param config La configuración de autenticación.
     * @return El AuthenticationManager.
     * @throws Exception Si ocurre un error al obtener el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Obtiene el AuthenticationManager
    }
}

