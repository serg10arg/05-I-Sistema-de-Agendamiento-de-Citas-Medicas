package com.example.citasmedicas.servicio;


import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.excepciones.ConflictoHorarioExcepcion;
import com.example.citasmedicas.dto.EspecialidadDTO;
import com.example.citasmedicas.modelo.entidad.Especialidad;
import com.example.citasmedicas.mapeador.EspecialidadMapeador;
import com.example.citasmedicas.repositorio.EspecialidadRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Manejo de transacciones

import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de Especialidades.
 * Contiene la lógica de negocio relacionada con las especialidades.
 */
@Service
public class EspecialidadServicio {

    private final EspecialidadRepositorio especialidadRepositorio;
    private final EspecialidadMapeador especialidadMapeador;

    public EspecialidadServicio(EspecialidadRepositorio especialidadRepositorio, EspecialidadMapeador especialidadMapeador) {
        this.especialidadRepositorio = especialidadRepositorio;
        this.especialidadMapeador = especialidadMapeador;
    }

    /**
     * Obtiene una especialidad por su ID.
     * @param id El UUID de la especialidad.
     * @return El DTO de la especialidad encontrada.
     * @throws RecursoNoEncontradoExcepcion Si la especialidad no existe.
     */
    @Transactional(readOnly = true) // Operación de solo lectura
    public EspecialidadDTO obtenerEspecialidadPorId(UUID id) {
        Especialidad especialidad = especialidadRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Especialidad no encontrada con ID: " + id));
        return especialidadMapeador.aDTO(especialidad);
    }

    /**
     * Obtiene una especialidad por su nombre.
     * @param nombre El nombre de la especialidad.
     * @return La entidad Especialidad.
     * @throws RecursoNoEncontradoExcepcion Si la especialidad no existe.
     */
    @Transactional(readOnly = true)
    public Especialidad obtenerEspecialidadPorNombre(String nombre) {
        return especialidadRepositorio.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Especialidad no encontrada con nombre: " + nombre));
    }

    /**
     * Obtiene la entidad Especialidad por su ID.
     * Este método es interno para que otros servicios trabajen directamente con la entidad.
     * @param id El UUID de la especialidad.
     * @return La entidad Especialidad.
     * @throws RecursoNoEncontradoExcepcion Si la especialidad no existe.
     */
    @Transactional(readOnly = true)
    public Especialidad obtenerEntidadEspecialidadPorId(UUID id) {
        return especialidadRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Especialidad no encontrada con ID: " + id));
    }
    /**
     * Obtiene todas las especialidades.
     * @return Una lista de DTOs de especialidades.
     */
    @Transactional(readOnly = true)
    public List<EspecialidadDTO> obtenerTodasLasEspecialidades() {
        List<Especialidad> especialidades = especialidadRepositorio.findAll();
        return especialidadMapeador.aListaDTO(especialidades);
    }

    /**
     * Crea una nueva especialidad.
     * @param especialidadDTO El DTO con los datos de la nueva especialidad.
     * @return El DTO de la especialidad creada.
     */
    @Transactional
    public EspecialidadDTO crearEspecialidad(EspecialidadDTO especialidadDTO) {
        // Validar si la especialidad ya existe por nombre
        if (especialidadRepositorio.findByNombre(especialidadDTO.getNombre()).isPresent()) {
            throw new ConflictoHorarioExcepcion("Ya existe una especialidad con el nombre: " + especialidadDTO.getNombre());
        }
        Especialidad especialidad = especialidadMapeador.aEntidad(especialidadDTO);
        // El ID es generado en el constructor de BaseEntidad, no es necesario setearlo aquí
        Especialidad especialidadGuardada = especialidadRepositorio.save(especialidad);
        return especialidadMapeador.aDTO(especialidadGuardada);
    }

    /**
     * Actualiza una especialidad existente.
     * @param id El UUID de la especialidad a actualizar.
     * @param especialidadDTO El DTO con los nuevos datos de la especialidad.
     * @return El DTO de la especialidad actualizada.
     * @throws RecursoNoEncontradoExcepcion Si la especialidad no existe.
     */
    @Transactional
    public EspecialidadDTO actualizarEspecialidad(UUID id, EspecialidadDTO especialidadDTO) {
        Especialidad especialidadExistente = especialidadRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Especialidad no encontrada con ID: " + id));

        // Validar si el nuevo nombre ya existe en otra especialidad
        especialidadRepositorio.findByNombre(especialidadDTO.getNombre()).ifPresent(e -> {
            if (!e.getId().equals(id)) { // Si es otra especialidad con el mismo nombre
                throw new ConflictoHorarioExcepcion("Ya existe otra especialidad con el nombre: " + especialidadDTO.getNombre());
            }
        });

        // Utilizar el mapeador para actualizar la entidad existente de forma segura
        especialidadMapeador.actualizarEntidad(especialidadDTO, especialidadExistente);
        Especialidad especialidadActualizada = especialidadRepositorio.save(especialidadExistente);
        return especialidadMapeador.aDTO(especialidadActualizada);
    }

    /**
     * Elimina una especialidad por su ID.
     * @param id El UUID de la especialidad a eliminar.
     * @throws RecursoNoEncontradoExcepcion Si la especialidad no existe.
     */
    @Transactional
    public void eliminarEspecialidad(UUID id) {
        if (!especialidadRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoExcepcion("Especialidad no encontrada con ID: " + id);
        }
        especialidadRepositorio.deleteById(id);
    }
}
