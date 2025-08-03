package com.example.citasmedicas.controlador;

import com.example.citasmedicas.dto.CrearDisponibilidadDTO;
import com.example.citasmedicas.dto.DisponibilidadDTO;
import com.example.citasmedicas.dto.DoctorDTO;
import com.example.citasmedicas.dto.DoctorListViewDTO;
import com.example.citasmedicas.dto.PaginacionMetadata;
import com.example.citasmedicas.dto.RespuestaPaginada;
import com.example.citasmedicas.excepciones.SolicitudInvalidaExcepcion;
import com.example.citasmedicas.servicio.DoctorServicio;
import com.example.citasmedicas.servicio.DisponibilidadServicio;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de Doctores.
 * Mapea las operaciones CRUD y búsquedas a endpoints HTTP.
 */
@RestController
@RequestMapping("/api/v1/doctores") // Versión de la API
public class DoctorControlador {

    private final DoctorServicio doctorServicio;
    private final DisponibilidadServicio disponibilidadServicio;

    public DoctorControlador(DoctorServicio doctorServicio, DisponibilidadServicio disponibilidadServicio) {
        this.doctorServicio = doctorServicio;
        this.disponibilidadServicio = disponibilidadServicio;
    }

    /**
     * Obtiene un doctor por su ID.
     * GET /api/v1/doctores/{doctorId}
     * @param id El UUID del doctor.
     * @return ResponseEntity con el DTO del doctor.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> obtenerDoctorPorId(@PathVariable("id") UUID id) {
        DoctorDTO doctor = doctorServicio.obtenerDoctorPorId(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Obtiene una lista de doctores, opcionalmente filtrada por especialidad, con paginación.
     * GET /api/v1/doctores?specialtyId={id}&limit={limit}&offset={offset}
     * @param especialidadId El UUID de la especialidad (opcional).
     * @param limit El número máximo de resultados por página (tamaño).
     * @param offset El desplazamiento de los resultados (número de página * tamaño).
     * @return ResponseEntity con la lista de DTOs de doctores y metadatos de paginación.
     */
    @GetMapping //
    public ResponseEntity<RespuestaPaginada<?>> obtenerDoctores(
            @RequestParam(value = "specialtyId", required = false) UUID especialidadId, //
            @RequestParam(defaultValue = "0") int offset, // offset se usa para calcular la página
            @RequestParam(defaultValue = "25") int limit // limit es el tamaño de la página
    ) {
        // Validación básica de limit/offset
        if (limit <= 0 || limit > 100) {
            throw new SolicitudInvalidaExcepcion("El parámetro 'limit' debe ser entre 1 y 100.");
        }
        if (offset < 0) {
            throw new SolicitudInvalidaExcepcion("El parámetro 'offset' no puede ser negativo.");
        }

        int pagina = offset / limit; // Calcular el número de página

        Page<?> paginaResultado;
        if (especialidadId != null) {
            paginaResultado = doctorServicio.buscarDoctoresPorEspecialidad(especialidadId, pagina, limit);
        } else {
            paginaResultado = doctorServicio.obtenerTodosLosDoctores(pagina, limit);
        }

        // Crear la respuesta con metadatos de paginación
        PaginacionMetadata metadata = new PaginacionMetadata(
                paginaResultado.getTotalElements(),
                paginaResultado.getTotalPages(),
                paginaResultado.getNumber(),
                paginaResultado.getSize()
        );

        RespuestaPaginada<?> respuesta = new RespuestaPaginada<>(paginaResultado.getContent(), metadata);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Crea un nuevo doctor.
     * POST /api/v1/doctores
     * @param doctorDTO El DTO con los datos del nuevo doctor.
     * @return ResponseEntity con el DTO del doctor creado y estado 201 Created.
     */
    @PostMapping
    public ResponseEntity<DoctorDTO> crearDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        DoctorDTO nuevoDoctor = doctorServicio.crearDoctor(doctorDTO);
        return new ResponseEntity<>(nuevoDoctor, HttpStatus.CREATED);
    }

    /**
     * Actualiza un doctor existente.
     * PUT /api/v1/doctores/{id}
     * @param id El UUID del doctor a actualizar.
     * @param doctorDTO El DTO con los nuevos datos del doctor.
     * @return ResponseEntity con el DTO del doctor actualizado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @autorizacionServicio.esPropietarioDeDoctor(authentication, #id))")
    public ResponseEntity<DoctorDTO> actualizarDoctor(@PathVariable("id") UUID id, @Valid @RequestBody DoctorDTO doctorDTO) {
        DoctorDTO doctorActualizado = doctorServicio.actualizarDoctor(id, doctorDTO);
        return ResponseEntity.ok(doctorActualizado);
    }

    /**
     * Elimina un doctor por su ID.
     * DELETE /api/v1/doctores/{id}
     * @param id El UUID del doctor a eliminar.
     * @return ResponseEntity sin contenido y estado 204 No Content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarDoctor(@PathVariable("id") UUID id) {
        doctorServicio.eliminarDoctor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene la disponibilidad de un doctor para un rango de fechas.
     * GET /api/v1/doctores/{doctorId}/disponibilidades?startDate={fecha}&endDate={fecha}
     * @param doctorId El ID del doctor.
     * @param startDate Fecha de inicio para la búsqueda (formato YYYY-MM-DD).
     * @param endDate Fecha de fin para la búsqueda (formato YYYY-MM-DD, opcional).
     * @return Lista de bloques de horarios disponibles.
     */
    @GetMapping("/{doctorId}/disponibilidades") //
    public ResponseEntity<List<DisponibilidadDTO>> obtenerHorariosDisponibles(
            @PathVariable UUID doctorId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDateTime startDate, //
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDateTime endDate
    ) {
        // Si endDate no se proporciona, se asume el final del día de startDate.
        LocalDateTime finBusqueda = (endDate != null) ? endDate.plusHours(23).plusMinutes(59).plusSeconds(59) : startDate.plusHours(23).plusMinutes(59).plusSeconds(59);

        List<DisponibilidadDTO> disponibilidades = disponibilidadServicio.obtenerDisponibilidadesDoctor(doctorId, startDate, finBusqueda);
        return ResponseEntity.ok(disponibilidades);
    }

    /**
     * Un doctor añade un nuevo bloque de tiempo de disponibilidad.
     * POST /api/v1/doctores/{doctorId}/disponibilidades
     * @param doctorId El ID del doctor.
     * @param crearDisponibilidadDTO DTO con la información del bloque de disponibilidad.
     * @return DTO del bloque de disponibilidad creado.
     */
    @PostMapping("/{doctorId}/disponibilidades") //
    @PreAuthorize("hasAuthority('DOCTOR') and @autorizacionServicio.esPropietarioDeDoctor(authentication, #doctorId)")
    public ResponseEntity<DisponibilidadDTO> crearBloqueDisponibilidad(
            @PathVariable UUID doctorId,
            @Valid @RequestBody CrearDisponibilidadDTO crearDisponibilidadDTO
    ) {
        // Asegurar que el doctorId del path coincida con el del payload
        if (!doctorId.equals(crearDisponibilidadDTO.getDoctorId())) {
            throw new SolicitudInvalidaExcepcion("El doctorId en la URL no coincide con el del cuerpo de la solicitud.");
        }
        DisponibilidadDTO nuevaDisponibilidad = disponibilidadServicio.crearDisponibilidad(crearDisponibilidadDTO);
        return new ResponseEntity<>(nuevaDisponibilidad, HttpStatus.CREATED);
    }

    /**
     * Elimina un bloque de disponibilidad (si no ha sido reservado).
     * DELETE /api/v1/doctores/disponibilidades/{disponibilidadId}
     * @param disponibilidadId El UUID del bloque de disponibilidad a eliminar.
     * @return ResponseEntity sin contenido y estado 204 No Content.
     */
    @DeleteMapping("/disponibilidades/{disponibilidadId}")
    @PreAuthorize("hasAuthority('DOCTOR') and @autorizacionServicio.esPropietarioDeDisponibilidad(authentication, #disponibilidadId)")
    public ResponseEntity<Void> eliminarBloqueDisponibilidad(@PathVariable UUID disponibilidadId) {
        disponibilidadServicio.eliminarDisponibilidad(disponibilidadId);
        return ResponseEntity.noContent().build();
    }
}
