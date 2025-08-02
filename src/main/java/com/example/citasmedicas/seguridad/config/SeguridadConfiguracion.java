package com.example.citasmedicas.seguridad.config;

import com.example.citasmedicas.seguridad.PuntoEntradaAutenticacionJwt;
import com.example.citasmedicas.seguridad.filtro.JwtAutenticacionFiltro;
import com.example.citasmedicas.seguridad.enumeracion.RolUsuario;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración de seguridad de Spring Security.
 * Define las políticas de autorización, el manejo de sesiones y el filtro JWT.
 */
@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize y @PostAuthorize
public class SeguridadConfiguracion {

    private final JwtAutenticacionFiltro jwtAutenticacionFiltro;
    private final AuthenticationProvider autenticacionProveedor;
    private final PuntoEntradaAutenticacionJwt puntoEntradaAutenticacionJwt;

    public SeguridadConfiguracion(JwtAutenticacionFiltro jwtAutenticacionFiltro,
                                  AuthenticationProvider autenticacionProveedor,
                                  PuntoEntradaAutenticacionJwt puntoEntradaAutenticacionJwt) {
        this.jwtAutenticacionFiltro = jwtAutenticacionFiltro;
        this.autenticacionProveedor = autenticacionProveedor;
        this.puntoEntradaAutenticacionJwt = puntoEntradaAutenticacionJwt;
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * @param http Objeto HttpSecurity para configurar la seguridad.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error de configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF, común para APIs REST sin sesiones [No en fuentes, práctica común]
                .exceptionHandling(exception -> exception.authenticationEntryPoint(puntoEntradaAutenticacionJwt)) // Maneja errores de autenticación
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso sin autenticación a /auth/** y /specialties (lista de especialidades)
                        .requestMatchers("/api/v1/auth/**", "/api/v1/especialidades").permitAll()
                        // Acceso para ADMIN a gestión de doctores y pacientes
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctores").hasAuthority(RolUsuario.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/doctores/{id}").hasAuthority(RolUsuario.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctores/{id}").hasAuthority(RolUsuario.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/pacientes").hasAuthority(RolUsuario.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pacientes/{id}").hasAuthority(RolUsuario.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pacientes/{id}").hasAuthority(RolUsuario.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/pacientes").hasAuthority(RolUsuario.ADMIN.name()) // ADMIN puede listar todos los pacientes
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctores").permitAll() // Doctores pueden ser listados por cualquiera

                        // Acceso para DOCTOR a gestión de su disponibilidad y ver sus citas
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctores/{doctorId}/disponibilidades").permitAll() // Cualquiera puede ver la disponibilidad de un doctor

                        // Acceso para PATIENT a agendar y gestionar sus propias citas y ver perfiles de doctor
                        .requestMatchers(HttpMethod.POST, "/api/v1/citas").hasAuthority(RolUsuario.PATIENT.name()) // Agendar cita

                        .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Gestión de sesión sin estado (para JWT)
                .authenticationProvider(autenticacionProveedor) // Establece el proveedor de autenticación personalizado
                .addFilterBefore(jwtAutenticacionFiltro, UsernamePasswordAuthenticationFilter.class); // Añade el filtro JWT antes del filtro de usuario/contraseña

        return http.build();
    }
}
