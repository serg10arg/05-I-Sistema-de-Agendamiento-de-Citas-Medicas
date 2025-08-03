package com.example.citasmedicas.controlador;

import com.example.citasmedicas.dto.ActualizarEstadoCitaDTO;
import com.example.citasmedicas.dto.CitaDTO;
import com.example.citasmedicas.dto.CrearCitaDTO;
import com.example.citasmedicas.servicio.CitaServicio;
import com.example.citasmedicas.modelo.entidad.EstadoCita;
import com.example.citasmedicas.servicio.DisponibilidadServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para la gestión de Citas.
 * Mapea las operaciones de agendamiento y cancelación a endpoints HTTP.
 */
@RestController
@RequestMapping("/api/v1/citas") // Versión de la API
public class CitaControlador {

    private final CitaServicio citaServicio;
    private final DisponibilidadServicio disponibilidadServicio; // Inyectar disponibilidad para eliminación

    public CitaControlador(CitaServicio citaServicio, DisponibilidadServicio disponibilidadServicio) {
        this.citaServicio = citaServicio;
        this.disponibilidadServicio = disponibilidadServicio;
    }

    /**
     * Agenda una nueva cita.
     * POST /api/v1/citas
     * @param crearCitaDTO DTO con los detalles de la cita a crear.
     * @return ResponseEntity con el DTO de la cita creada y estado 201 Created.
     */
    @PostMapping //
    public ResponseEntity<CitaDTO> agendarCita(@Valid @RequestBody CrearCitaDTO crearCitaDTO) {
        CitaDTO nuevaCita = citaServicio.agendarCita(crearCitaDTO);
        return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED); // 201 Created
    }

    /**
     * Cancela una cita.
     * PATCH /api/v1/citas/{citaId}
     * @param citaId El UUID de la cita a cancelar.
     * @param actualizarEstadoCitaDTO DTO que contiene el nuevo estado (se espera CANCELADA).
     * @return ResponseEntity con el DTO de la cita actualizada.
     */
    @PatchMapping("/{citaId}") //
    @PreAuthorize("hasAuthority('PATIENT') and @autorizacionServicio.esPropietarioDeCita(authentication, #citaId)")
    public ResponseEntity<CitaDTO> cancelarCita(
            @PathVariable UUID citaId,
            @Valid @RequestBody ActualizarEstadoCitaDTO actualizarEstadoCitaDTO
    ) {
        // Solo permitir la cancelación, no otros cambios de estado via PATCH en este endpoint específico.
        if (actualizarEstadoCitaDTO.getNuevoEstado() != EstadoCita.CANCELADA) {
            throw new IllegalArgumentException("Solo se permite cancelar citas a través de este endpoint. El estado enviado no es CANCELADA.");
        }
        CitaDTO citaActualizada = citaServicio.cancelarCita(citaId);
        return ResponseEntity.ok(citaActualizada);
    }

    /**
     * Obtiene una cita específica por su ID.
     * GET /api/v1/citas/{citaId}
     * @param citaId El UUID de la cita.
     * @return ResponseEntity con el DTO de la cita.
     */
    @GetMapping("/{citaId}")
    @PreAuthorize("@autorizacionServicio.esPropietarioDeCita(authentication, #citaId) or hasAuthority('ADMIN')")
    public ResponseEntity<CitaDTO> obtenerCitaPorId(@PathVariable UUID citaId) {
        CitaDTO cita = citaServicio.obtenerCitaPorId(citaId);
        return ResponseEntity.ok(cita);
    }
}
