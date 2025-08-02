package com.example.citasmedicas.servicio;

import com.example.citasmedicas.excepciones.ConflictoHorarioExcepcion;
import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.dto.PacienteDTO;
import com.example.citasmedicas.modelo.entidad.Paciente;
import com.example.citasmedicas.mapeador.PacienteMapeador;
import com.example.citasmedicas.repositorio.PacienteRepositorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio para la gestión de Pacientes.
 * Contiene la lógica de negocio relacionada con los pacientes.
 */
@Service
public class PacienteServicio {

    private final PacienteRepositorio pacienteRepositorio;
    private final PacienteMapeador pacienteMapeador;

    public PacienteServicio(PacienteRepositorio pacienteRepositorio, PacienteMapeador pacienteMapeador) {
        this.pacienteRepositorio = pacienteRepositorio;
        this.pacienteMapeador = pacienteMapeador;
    }

    /**
     * Obtiene un paciente por su ID.
     * @param id El UUID del paciente.
     * @return El DTO del paciente encontrado.
     * @throws RecursoNoEncontradoExcepcion Si el paciente no existe.
     */
    @Transactional(readOnly = true)
    public PacienteDTO obtenerPacientePorId(UUID id) {
        Paciente paciente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente no encontrado con ID: " + id));
        return pacienteMapeador.aDTO(paciente);
    }

    /**
     * Obtiene la entidad Paciente por su ID.
     * Este método es interno para que otros servicios trabajen directamente con la entidad.
     * @param id El UUID del paciente.
     * @return La entidad Paciente.
     * @throws RecursoNoEncontradoExcepcion Si el paciente no existe.
     */
    @Transactional(readOnly = true)
    public Paciente obtenerEntidadPacientePorId(UUID id) {
        return pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los pacientes con paginación.
     * @param pagina El número de página (base 0).
     * @param tamano El tamaño de la página.
     * @return Una Page de DTOs de pacientes con metadatos de paginación.
     */
    @Transactional(readOnly = true)
    public Page<PacienteDTO> obtenerTodosLosPacientes(int pagina, int tamano) {
        Pageable paginacion = PageRequest.of(pagina, tamano);
        Page<Paciente> pacientes = pacienteRepositorio.findAll(paginacion);
        return pacientes.map(pacienteMapeador::aDTO);
    }

    /**
     * Crea un nuevo paciente.
     * @param pacienteDTO El DTO con los datos del nuevo paciente.
     * @return El DTO del paciente creado.
     */
    @Transactional
    public PacienteDTO crearPaciente(PacienteDTO pacienteDTO) {
        // Validar si el email ya existe
        if (pacienteRepositorio.findByEmail(pacienteDTO.email()).isPresent()) {
            throw new ConflictoHorarioExcepcion("Ya existe un paciente con el email: " + pacienteDTO.email());
        }
        Paciente paciente = pacienteMapeador.aEntidad(pacienteDTO);
        Paciente pacienteGuardado = pacienteRepositorio.save(paciente);
        return pacienteMapeador.aDTO(pacienteGuardado);
    }

    /**
     * Actualiza un paciente existente.
     * @param id El UUID del paciente a actualizar.
     * @param pacienteDTO El DTO con los nuevos datos del paciente.
     * @return El DTO del paciente actualizado.
     * @throws RecursoNoEncontradoExcepcion Si el paciente no existe.
     */
    @Transactional
    public PacienteDTO actualizarPaciente(UUID id, PacienteDTO pacienteDTO) {
        Paciente pacienteExistente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente no encontrado con ID: " + id));

        // Validar si el nuevo email ya existe en otro paciente
        pacienteRepositorio.findByEmail(pacienteDTO.email()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new ConflictoHorarioExcepcion("Ya existe otro paciente con el email: " + pacienteDTO.email());
            }
        });

        // Utilizar el mapeador para actualizar la entidad existente de forma segura
        pacienteMapeador.actualizarEntidad(pacienteDTO, pacienteExistente);
        Paciente pacienteActualizado = pacienteRepositorio.save(pacienteExistente);
        return pacienteMapeador.aDTO(pacienteActualizado);
    }

    /**
     * Elimina un paciente por su ID.
     * @param id El UUID del paciente a eliminar.
     * @throws RecursoNoEncontradoExcepcion Si el paciente no existe.
     */
    @Transactional
    public void eliminarPaciente(UUID id) {
        if (!pacienteRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoExcepcion("Paciente no encontrado con ID: " + id);
        }
        pacienteRepositorio.deleteById(id);
    }
}
