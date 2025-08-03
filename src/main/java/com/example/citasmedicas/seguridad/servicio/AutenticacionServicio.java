package com.example.citasmedicas.seguridad.servicio;

import com.example.citasmedicas.modelo.entidad.Paciente;
import com.example.citasmedicas.repositorio.PacienteRepositorio;
import com.example.citasmedicas.seguridad.modelo.AutenticacionSolicitud;
import com.example.citasmedicas.seguridad.modelo.AutenticacionRespuesta;
import com.example.citasmedicas.seguridad.modelo.RegistroPacienteSolicitud;
import com.example.citasmedicas.seguridad.enumeracion.RolUsuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Manejo de transacciones

/**
 * Servicio para la lógica de autenticación de usuarios.
 * Maneja el registro de nuevos usuarios y el inicio de sesión.
 */
@Service
public class AutenticacionServicio {

    private final PacienteRepositorio pacienteRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicio jwtServicio;
    private final AuthenticationManager authenticationManager;

    public AutenticacionServicio(PacienteRepositorio pacienteRepositorio,
                                 PasswordEncoder passwordEncoder, JwtServicio jwtServicio,
                                 AuthenticationManager authenticationManager) {
        this.pacienteRepositorio = pacienteRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.jwtServicio = jwtServicio;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     * Se asigna el rol 'PATIENT' y se genera un token JWT.
     * @param request DTO con los datos del paciente para el registro.
     * @return AutenticacionRespuesta que contiene el token JWT.
     */
    @Transactional
    public AutenticacionRespuesta registrarPaciente(RegistroPacienteSolicitud request) {
        // Validar si el email ya existe
        if (pacienteRepositorio.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un paciente con el email: " + request.getEmail());
        }

        // Crear la nueva entidad Paciente a partir del DTO de registro
        Paciente paciente = new Paciente();
        paciente.setPrimerNombre(request.getPrimerNombre());
        paciente.setApellido(request.getApellido());
        paciente.setEmail(request.getEmail());
        paciente.setTelefono(request.getTelefono());
        paciente.setContrasena(passwordEncoder.encode(request.getContrasena())); // Codificar la contraseña

        Paciente pacienteGuardado = pacienteRepositorio.save(paciente); // Guardar el paciente

        // Generar token para el nuevo paciente. Asumimos el email como el nombre de usuario.
        var token = jwtServicio.generarToken(pacienteGuardado.getEmail()); // Usar el email del paciente guardado
        return AutenticacionRespuesta.builder().token(token).build();
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     * @param request Solicitud de autenticación con email y contraseña.
     * @return AutenticacionRespuesta que contiene el token JWT.
     */
    @Transactional(readOnly = true)
    public AutenticacionRespuesta autenticar(AutenticacionSolicitud request) {
        // Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getContrasena()
                )
        );

        // Si la autenticación es exitosa, generar un token JWT para el usuario.
        var token = jwtServicio.generarToken(request.getEmail()); // El email es el nombre de usuario.
        return AutenticacionRespuesta.builder().token(token).build();
    }
}
