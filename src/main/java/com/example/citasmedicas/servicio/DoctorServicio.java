package com.example.citasmedicas.servicio;

import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.dto.DoctorDTO;
import com.example.citasmedicas.dto.DoctorListViewDTO;
import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Especialidad;
import com.example.citasmedicas.mapeador.DoctorMapeador;
import com.example.citasmedicas.repositorio.DoctorRepositorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Manejo de transacciones

import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de Doctores.
 * Contiene la lógica de negocio relacionada con los doctores.
 */
@Service
public class DoctorServicio {

    private final DoctorRepositorio doctorRepositorio;
    private final DoctorMapeador doctorMapeador;
    private final EspecialidadServicio especialidadServicio; // Necesario para buscar especialidades

    public DoctorServicio(DoctorRepositorio doctorRepositorio, DoctorMapeador doctorMapeador, EspecialidadServicio especialidadServicio) {
        this.doctorRepositorio = doctorRepositorio;
        this.doctorMapeador = doctorMapeador;
        this.especialidadServicio = especialidadServicio;
    }

    /**
     * Obtiene un doctor por su ID.
     * @param id El UUID del doctor.
     * @return El DTO del doctor encontrado.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no existe.
     */
    @Transactional(readOnly = true)
    public DoctorDTO obtenerDoctorPorId(UUID id) {
        Doctor doctor = doctorRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Doctor no encontrado con ID: " + id));
        return doctorMapeador.aDTO(doctor);
    }

    /**
     * Obtiene la entidad Doctor por su ID.
     * Este método es interno para que otros servicios trabajen directamente con la entidad.
     * @param id El UUID del doctor.
     * @return La entidad Doctor.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no existe.
     */
    @Transactional(readOnly = true)
    public Doctor obtenerEntidadDoctorPorId(UUID id) {
        return doctorRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Doctor no encontrado con ID: " + id));
    }


    /**
     * Obtiene todos los doctores con paginación.
     * @param pagina El número de página (base 0).
     * @param tamano El tamaño de la página (limit).
     * @return Una Page de DTOs de doctores con metadatos de paginación.
     */
    @Transactional(readOnly = true)
    public Page<DoctorDTO> obtenerTodosLosDoctores(int pagina, int tamano) {
        Pageable paginacion = PageRequest.of(pagina, tamano); //
        Page<Doctor> doctores = doctorRepositorio.findAll(paginacion);
        return doctores.map(doctorMapeador::aDTO); // Mapea la página de entidades a una página de DTOs
    }

    /**
     * Busca doctores por especialidad con paginación.
     * @param especialidadId El UUID de la especialidad.
     * @param pagina El número de página (base 0).
     * @param tamano El tamaño de la página.
     * @return Una Page de DTOs de vista de lista de doctores.
     * @throws RecursoNoEncontradoExcepcion Si la especialidad no existe.
     */
    @Transactional(readOnly = true)
    public Page<DoctorListViewDTO> buscarDoctoresPorEspecialidad(UUID especialidadId, int pagina, int tamano) {
        Especialidad especialidad = especialidadServicio.obtenerEntidadEspecialidadPorId(especialidadId);
        Pageable paginacion = PageRequest.of(pagina, tamano);
        return doctorRepositorio.findByEspecialidad(especialidad, paginacion);
    }

    /**
     * Crea un nuevo doctor.
     * @param doctorDTO El DTO con los datos del nuevo doctor.
     * @return El DTO del doctor creado.
     */
    @Transactional
    public DoctorDTO crearDoctor(DoctorDTO doctorDTO) {
        Especialidad especialidad = especialidadServicio.obtenerEntidadEspecialidadPorId(doctorDTO.getEspecialidad().getId());
        Doctor doctor = doctorMapeador.aEntidad(doctorDTO);
        doctor.setEspecialidad(especialidad);
        Doctor doctorGuardado = doctorRepositorio.save(doctor);
        return doctorMapeador.aDTO(doctorGuardado);
    }

    /**
     * Actualiza un doctor existente.
     * @param id El UUID del doctor a actualizar.
     * @param doctorDTO El DTO con los nuevos datos del doctor.
     * @return El DTO del doctor actualizado.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no existe.
     */
    @Transactional
    public DoctorDTO actualizarDoctor(UUID id, DoctorDTO doctorDTO) {
        Doctor doctorExistente = doctorRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Doctor no encontrado con ID: " + id));

        // Utilizar el mapeador para actualizar la entidad existente de forma segura
        doctorMapeador.actualizarEntidad(doctorDTO, doctorExistente);
        // La especialidad es una relación, se gestiona por separado
        doctorExistente.setEspecialidad(especialidadServicio.obtenerEntidadEspecialidadPorId(doctorDTO.getEspecialidad().getId()));

        Doctor doctorActualizado = doctorRepositorio.save(doctorExistente);
        return doctorMapeador.aDTO(doctorActualizado);
    }

    /**
     * Elimina un doctor por su ID.
     * @param id El UUID del doctor a eliminar.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no existe.
     */
    @Transactional
    public void eliminarDoctor(UUID id) {
        if (!doctorRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoExcepcion("Doctor no encontrado con ID: " + id);
        }
        doctorRepositorio.deleteById(id);
    }
}
