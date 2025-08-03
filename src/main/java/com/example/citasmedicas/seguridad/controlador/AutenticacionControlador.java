package com.example.citasmedicas.seguridad.controlador;

import com.example.citasmedicas.seguridad.modelo.AutenticacionSolicitud;
import com.example.citasmedicas.seguridad.modelo.AutenticacionRespuesta;
import com.example.citasmedicas.seguridad.modelo.RegistroPacienteSolicitud;
import com.example.citasmedicas.seguridad.servicio.AutenticacionServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la autenticación de usuarios.
 * Maneja el registro y el inicio de sesión.
 */
@RestController
@RequestMapping("/api/v1/auth") // Versión de la API
public class AutenticacionControlador {

    private final AutenticacionServicio autenticacionServicio;

    public AutenticacionControlador(AutenticacionServicio autenticacionServicio) {
        this.autenticacionServicio = autenticacionServicio;
    }

    /**
     * Endpoint para registrar un nuevo paciente.
     * @param request DTO con los datos del paciente para registro.
     * @return ResponseEntity con la respuesta de autenticación (JWT).
     */
    @PostMapping("/registro/paciente")
    public ResponseEntity<AutenticacionRespuesta> registrarPaciente(
            @Valid @RequestBody RegistroPacienteSolicitud request
    ) {
        AutenticacionRespuesta respuesta = autenticacionServicio.registrarPaciente(request);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED); // Devolver 201 Created
    }

    /**
     * Endpoint para iniciar sesión (autenticación).
     * @param request DTO con el email y contraseña del usuario.
     * @return ResponseEntity con la respuesta de autenticación (JWT).
     */
    @PostMapping("/autenticar")
    public ResponseEntity<AutenticacionRespuesta> autenticar(
            @Valid @RequestBody AutenticacionSolicitud request
    ) {
        return ResponseEntity.ok(autenticacionServicio.autenticar(request));
    }
}
