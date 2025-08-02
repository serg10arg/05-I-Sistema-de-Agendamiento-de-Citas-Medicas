package com.example.citasmedicas.seguridad.servicio;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación y validación de JSON Web Tokens (JWT).
 * Utiliza la librería JJWT.
 */
@Service
public class JwtServicio {

    // Se carga la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String CLAVE_SECRETA;

    // Se carga el tiempo de expiración desde application.properties
    @Value("${jwt.expiration}")
    private long TIEMPO_EXPIRACION;

    /**
     * Genera un token JWT para un nombre de usuario dado.
     * @param nombreUsuario El nombre de usuario (subject) para el token.
     * @return El token JWT generado.
     */
    public String generarToken(String nombreUsuario) {
        Map<String, Object> claims = new HashMap<>(); // Claims adicionales, si los hubiera
        return crearToken(claims, nombreUsuario);
    }

    /**
     * Crea el token JWT con claims, sujeto y fecha de expiración.
     * @param claims Claims adicionales.
     * @param subject El sujeto (nombre de usuario).
     * @return El token JWT.
     */
    private String crearToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Establece los claims
                .setSubject(subject) // Establece el sujeto
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION)) // Fecha de expiración
                .signWith(getSigningKey()) // Firma el token con la clave secreta. El algoritmo se infiere de la clave (HS256).
                .compact(); // Construye el token
    }

    /**
     * Obtiene la clave de firma decodificada a partir de la CLAVE_SECRETA base64.
     * @return La clave de firma.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(CLAVE_SECRETA); // Decodifica la clave base64
        return Keys.hmacShaKeyFor(keyBytes); // Crea una clave HMAC para la firma
    }

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public String extraerNombreUsuario(String token) {
        return extraerClaim(token, Claims::getSubject); // Extrae el 'subject'
    }

    /**
     * Extrae un claim específico del token JWT.
     * @param token El token JWT.
     * @param claimsResolver Función para resolver el claim (e.g., Claims::getExpiration).
     * @param <T> El tipo del claim a extraer.
     * @return El valor del claim.
     */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token); // Obtiene todos los claims
        return claimsResolver.apply(claims); // Aplica la función para obtener el claim específico
    }

    /**
     * Extrae todos los claims del token JWT.
     * Realiza la validación de la firma del token.
     * @param token El token JWT.
     * @return Los claims del token.
     */
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // Establece la clave de firma para la validación.
                .build()
                .parseClaimsJws(token) // Parsea y valida la firma
                .getBody(); // Obtiene el cuerpo del token (los claims)
    }

    /**
     * Valida si un token JWT es válido para un nombre de usuario dado.
     * @param token El token JWT.
     * @param nombreUsuario El nombre de usuario a validar.
     * @return True si el token es válido y no ha expirado, false de lo contrario.
     */
    public boolean esTokenValido(String token, String nombreUsuario) {
        final String usernameExtraido = extraerNombreUsuario(token);
        return (usernameExtraido.equals(nombreUsuario) && !esTokenExpirado(token));
    }

    /**
     * Verifica si el token JWT ha expirado.
     * @param token El token JWT.
     * @return True si el token ha expirado, false de lo contrario.
     */
    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }
}
