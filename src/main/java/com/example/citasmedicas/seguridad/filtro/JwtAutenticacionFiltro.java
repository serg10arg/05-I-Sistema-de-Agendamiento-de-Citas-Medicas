package com.example.citasmedicas.seguridad.filtro;

import com.example.citasmedicas.seguridad.servicio.JwtServicio;
import io.jsonwebtoken.JwtException;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta una vez por cada petición para validar los tokens JWT.
 * Intercepta las solicitudes, extrae el token JWT y autentica al usuario si el token es válido.
 */
@Component
public class JwtAutenticacionFiltro extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAutenticacionFiltro.class);

    private final JwtServicio jwtServicio;
    private final UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario

    public JwtAutenticacionFiltro(JwtServicio jwtServicio, UserDetailsService userDetailsService) {
        this.jwtServicio = jwtServicio;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // Petición HTTP
            @NonNull HttpServletResponse response, // Respuesta HTTP
            @NonNull FilterChain filterChain // Cadena de filtros
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // Obtener el encabezado de autorización
        final String jwt;
        final String nombreUsuario;

        // Si el encabezado no existe o no empieza con "Bearer ", no hay token JWT.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Pasa la petición al siguiente filtro
            return;
        }

        // Extraer el token JWT (después de "Bearer ")
        jwt = authHeader.substring(7);

        try {
            nombreUsuario = jwtServicio.extraerNombreUsuario(jwt); // Extraer el nombre de usuario del token

            // Si el nombre de usuario no es nulo y no hay una autenticación actual en el contexto de seguridad
            if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(nombreUsuario); // Cargar los detalles del usuario

                // Validar el token
                if (jwtServicio.esTokenValido(jwt, userDetails.getUsername())) {
                    // Si el token es válido, crear un objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credenciales nulas, ya que el token ya valida la identidad
                            userDetails.getAuthorities() // Roles/autoridades del usuario
                    );
                    // Establecer los detalles de la autenticación desde la petición
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Establecer la autenticación en el contexto de seguridad para la solicitud actual
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
        }
        filterChain.doFilter(request, response); // Continúa con la cadena de filtros
    }
}
