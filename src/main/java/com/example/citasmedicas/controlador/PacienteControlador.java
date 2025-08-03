package com.example.citasmedicas.controlador;

import com.example.citasmedicas.dto.CitaDTO;
import com.example.citasmedicas.dto.PacienteDTO;
import com.example.citasmedicas.dto.PaginacionMetadata;
import com.example.citasmedicas.servicio.CitaServicio;
import com.example.citasmedicas.servicio.PacienteServicio;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de Pacientes.
 * Mapea las operaciones CRUD y las búsquedas a endpoints HTTP.
 */
@RestController
@RequestMapping("/api/v1/pacientes") // Versión de la API
public class PacienteControlador {

    private final PacienteServicio pacienteServicio;
    private final CitaServicio citaServicio;

    public PacienteControlador(PacienteServicio pacienteServicio, CitaServicio citaServicio) {
        this.pacienteServicio = pacienteServicio;
        this.citaServicio = citaServicio;
    }

    /**
     * Obtiene un paciente por su ID.
     * GET /api/v1/pacientes/{id}
     * @param id El UUID del paciente.
     * @return ResponseEntity con el DTO del paciente.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> obtenerPacientePorId(@PathVariable UUID id) {
        PacienteDTO paciente = pacienteServicio.obtenerPacientePorId(id);
        return ResponseEntity.ok(paciente);
    }

    /**
     * Obtiene una lista de todos los pacientes, con paginación (solo para administradores).
     * GET /api/v1/pacientes?limit={limit}&offset={offset}
     * @param limit El número máximo de resultados por página.
     * @param offset El desplazamiento de los resultados.
     * @return ResponseEntity con la lista de DTOs de pacientes y metadatos de paginación.
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosPacientes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "25") int limit
    ) {
        // Validación básica de limit/offset
        if (limit <= 0 || limit > 100) {
            return ResponseEntity.badRequest().body("El parámetro 'limit' debe ser entre 1 y 100.");
        }
        if (offset < 0) {
            return ResponseEntity.badRequest().body("El parámetro 'offset' no puede ser negativo.");
        }

        int pagina = offset / limit;

        Page<PacienteDTO> paginaPacientes = pacienteServicio.obtenerTodosLosPacientes(pagina, limit);

        PaginacionMetadata metadata = new PaginacionMetadata(
                paginaPacientes.getTotalElements(),
                paginaPacientes.getTotalPages(),
                paginaPacientes.getNumber(),
                paginaPacientes.getSize()
        );

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(metadata.getTotalElementos()))
                .header("X-Total-Pages", String.valueOf(metadata.getTotalPaginas()))
                .header("X-Current-Page", String.valueOf(metadata.getPaginaActual()))
                .body(paginaPacientes.getContent());
    }

    /**
     * Crea un nuevo paciente.
     * POST /api/v1/pacientes
     * @param pacienteDTO El DTO con los datos del nuevo paciente.
     * @return ResponseEntity con el DTO del paciente creado y estado 201 Created.
     */
    @PostMapping
    public ResponseEntity<PacienteDTO> crearPaciente(@Valid @RequestBody PacienteDTO pacienteDTO) {
        PacienteDTO nuevoPaciente = pacienteServicio.crearPaciente(pacienteDTO);
        return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
    }

    /**
     * Actualiza un paciente existente.
     * PUT /api/v1/pacientes/{id}
     * @param id El UUID del paciente a actualizar.
     * @param pacienteDTO El DTO con los nuevos datos del paciente.
     * @return ResponseEntity con el DTO del paciente actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> actualizarPaciente(@PathVariable UUID id, @Valid @RequestBody PacienteDTO pacienteDTO) {
        PacienteDTO pacienteActualizado = pacienteServicio.actualizarPaciente(id, pacienteDTO);
        return ResponseEntity.ok(pacienteActualizado);
    }

    /**
     * Elimina un paciente por su ID.
     * DELETE /api/v1/pacientes/{id}
     * @param id El UUID del paciente a eliminar.
     * @return ResponseEntity sin contenido y estado 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable UUID id) {
        pacienteServicio.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene las citas programadas de un paciente.
     * GET /api/v1/pacientes/{patientId}/appointments
     * @param patientId El UUID del paciente.
     * @param limit El número máximo de resultados por página.
     * @param offset El desplazamiento de los resultados.
     * @return ResponseEntity con la lista de DTOs de citas y metadatos de paginación.
     */
    @GetMapping("/{patientId}/citas") //
    public ResponseEntity<?> obtenerCitasPaciente(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "25") int limit
    ) {
        // Validación básica de limit/offset
        if (limit <= 0 || limit > 100) {
            return ResponseEntity.badRequest().body("El parámetro 'limit' debe ser entre 1 y 100.");
        }
        if (offset < 0) {
            return ResponseEntity.badRequest().body("El parámetro 'offset' no puede ser negativo.");
        }

        int pagina = offset / limit;

        Page<CitaDTO> paginaCitas = citaServicio.obtenerCitasPorPaciente(patientId, pagina, limit);

        PaginacionMetadata metadata = new PaginacionMetadata(
                paginaCitas.getTotalElements(),
                paginaCitas.getTotalPages(),
                paginaCitas.getNumber(),
                paginaCitas.getSize()
        );

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(metadata.getTotalElementos()))
                .header("X-Total-Pages", String.valueOf(metadata.getTotalPaginas()))
                .header("X-Current-Page", String.valueOf(metadata.getPaginaActual()))
                .body(paginaCitas.getContent());
    }
}

