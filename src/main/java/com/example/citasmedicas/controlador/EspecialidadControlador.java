package com.example.citasmedicas.controlador;

import com.example.citasmedicas.dto.EspecialidadDTO;
import com.example.citasmedicas.servicio.EspecialidadServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de Especialidades.
 * Mapea las operaciones CRUD a endpoints HTTP.
 */
@RestController
@RequestMapping("/api/v1/especialidades") // Versión de la API
public class EspecialidadControlador {

    private final EspecialidadServicio especialidadServicio;

    public EspecialidadControlador(EspecialidadServicio especialidadServicio) {
        this.especialidadServicio = especialidadServicio;
    }

    /**
     * Obtiene una especialidad por su ID.
     * GET /api/v1/especialidades/{id}
     * @param id El UUID de la especialidad.
     * @return ResponseEntity con el DTO de la especialidad.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> obtenerEspecialidadPorId(@PathVariable UUID id) {
        EspecialidadDTO especialidad = especialidadServicio.obtenerEspecialidadPorId(id);
        return ResponseEntity.ok(especialidad);
    }

    /**
     * Obtiene todas las especialidades.
     * GET /api/v1/especialidades
     * @return ResponseEntity con una lista de DTOs de especialidades.
     */
    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> obtenerTodasLasEspecialidades() {
        List<EspecialidadDTO> especialidades = especialidadServicio.obtenerTodasLasEspecialidades();
        return ResponseEntity.ok(especialidades);
    }

    /**
     * Crea una nueva especialidad.
     * POST /api/v1/especialidades
     * @param especialidadDTO El DTO con los datos de la nueva especialidad.
     * @return ResponseEntity con el DTO de la especialidad creada y estado 201 Created.
     */
    @PostMapping
    public ResponseEntity<EspecialidadDTO> crearEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO) {
        EspecialidadDTO nuevaEspecialidad = especialidadServicio.crearEspecialidad(especialidadDTO);
        return new ResponseEntity<>(nuevaEspecialidad, HttpStatus.CREATED);
    }

    /**
     * Actualiza una especialidad existente.
     * PUT /api/v1/especialidades/{id}
     * @param id El UUID de la especialidad a actualizar.
     * @param especialidadDTO El DTO con los nuevos datos de la especialidad.
     * @return ResponseEntity con el DTO de la especialidad actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> actualizarEspecialidad(@PathVariable UUID id, @Valid @RequestBody EspecialidadDTO especialidadDTO) {
        EspecialidadDTO especialidadActualizada = especialidadServicio.actualizarEspecialidad(id, especialidadDTO);
        return ResponseEntity.ok(especialidadActualizada);
    }

    /**
     * Elimina una especialidad por su ID.
     * DELETE /api/v1/especialidades/{id}
     * @param id El UUID de la especialidad a eliminar.
     * @return ResponseEntity sin contenido y estado 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable UUID id) {
        especialidadServicio.eliminarEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}

