package com.example.citasmedicas.servicio;

import com.example.citasmedicas.excepciones.ConflictoHorarioExcepcion;
import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.excepciones.SolicitudInvalidaExcepcion;
import com.example.citasmedicas.dto.CrearDisponibilidadDTO;
import com.example.citasmedicas.dto.DisponibilidadDTO;
import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Disponibilidad;
import com.example.citasmedicas.mapeador.DisponibilidadMapeador;
import com.example.citasmedicas.repositorio.DisponibilidadRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de Disponibilidades de los doctores.
 */
@Service
public class DisponibilidadServicio {

    private final DisponibilidadRepositorio disponibilidadRepositorio;
    private final DisponibilidadMapeador disponibilidadMapeador;
    private final DoctorServicio doctorServicio;

    public DisponibilidadServicio(DisponibilidadRepositorio disponibilidadRepositorio, DisponibilidadMapeador disponibilidadMapeador, DoctorServicio doctorServicio) {
        this.disponibilidadRepositorio = disponibilidadRepositorio;
        this.disponibilidadMapeador = disponibilidadMapeador;
        this.doctorServicio = doctorServicio;
    }

    /**
     * Crea un nuevo bloque de disponibilidad para un doctor.
     * @param dto El DTO con la información del nuevo bloque.
     * @return El DTO del bloque de disponibilidad creado.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no es encontrado.
     * @throws SolicitudInvalidaExcepcion Si la hora de inicio es posterior a la hora de fin o hay solapamiento.
     */
    @Transactional
    public DisponibilidadDTO crearDisponibilidad(CrearDisponibilidadDTO dto) {
        // 1. Verificar que la hora de inicio sea anterior a la hora de fin
        if (dto.getHoraInicio().isAfter(dto.getHoraFin())) {
            throw new SolicitudInvalidaExcepcion("La hora de inicio no puede ser posterior a la hora de fin.");
        }

        Doctor doctor = doctorServicio.obtenerEntidadDoctorPorId(dto.getDoctorId()); // Obtener la entidad Doctor

        // 2. Verificar que no haya solapamiento con disponibilidades existentes del mismo doctor
        List<Disponibilidad> disponibilidadesSolapadas = disponibilidadRepositorio
                .findByDoctorAndHoraFinAfterAndHoraInicioBefore(doctor, dto.getHoraInicio(), dto.getHoraFin());
        if (!disponibilidadesSolapadas.isEmpty()) {
            throw new SolicitudInvalidaExcepcion("El horario de disponibilidad se solapa con una disponibilidad existente.");
        }

        Disponibilidad disponibilidad = disponibilidadMapeador.aEntidad(dto);
        disponibilidad.setDoctor(doctor); // Asignar el doctor a la disponibilidad

        Disponibilidad disponibilidadGuardada = disponibilidadRepositorio.save(disponibilidad);
        return disponibilidadMapeador.aDTO(disponibilidadGuardada);
    }

    /**
     * Obtiene los bloques de disponibilidad de un doctor en un rango de fechas.
     * @param doctorId El UUID del doctor.
     * @param fechaInicio La fecha de inicio del rango.
     * @param fechaFin La fecha de fin del rango.
     * @return Una lista de DTOs de disponibilidad.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no es encontrado.
     */
    @Transactional(readOnly = true)
    public List<DisponibilidadDTO> obtenerDisponibilidadesDoctor(UUID doctorId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Doctor doctor = doctorServicio.obtenerEntidadDoctorPorId(doctorId);
        List<Disponibilidad> disponibilidades = disponibilidadRepositorio
                .findByDoctorAndHoraInicioBetweenOrderByHoraInicioAsc(doctor, fechaInicio, fechaFin); //
        return disponibilidadMapeador.aListaDTO(disponibilidades);
    }

    /**
     * Obtiene la entidad Disponibilidad por su ID, asegurando que no esté reservada.
     * Este método es interno para que otros servicios trabajen directamente con la entidad.
     * @param disponibilidadId El UUID de la disponibilidad.
     * @return La entidad Disponibilidad.
     * @throws RecursoNoEncontradoExcepcion Si la disponibilidad no existe o ya está reservada.
     */
    @Transactional(readOnly = true)
    public Disponibilidad obtenerEntidadDisponibilidadDisponiblePorId(UUID disponibilidadId) {
        return disponibilidadRepositorio.findByIdAndEstaReservadoFalse(disponibilidadId) //
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Bloque de disponibilidad no encontrado o ya reservado con ID: " + disponibilidadId));
    }

    /**
     * Elimina un bloque de disponibilidad por su ID.
     * Solo se puede eliminar si no está reservado.
     * @param id El UUID de la disponibilidad a eliminar.
     * @throws RecursoNoEncontradoExcepcion Si la disponibilidad no existe.
     * @throws SolicitudInvalidaExcepcion Si la disponibilidad ya está reservada.
     */
    @Transactional
    public void eliminarDisponibilidad(UUID id) {
        Disponibilidad disponibilidad = disponibilidadRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Disponibilidad no encontrada con ID: " + id));

        if (disponibilidad.getEstaReservado()) { // No eliminar si está reservada
            throw new SolicitudInvalidaExcepcion("No se puede eliminar un bloque de disponibilidad que ya ha sido reservado.");
        }
        disponibilidadRepositorio.delete(disponibilidad);
    }

    /**
     * Marca un bloque de disponibilidad como reservado.
     * Este método es para uso transaccional por otros servicios.
     * @param disponibilidad La entidad Disponibilidad a reservar.
     */
    @Transactional
    public void reservarDisponibilidad(Disponibilidad disponibilidad) {
        if (disponibilidad.getEstaReservado()) {
            throw new ConflictoHorarioExcepcion("El bloque de disponibilidad con ID " + disponibilidad.getId() + " ya se encuentra reservado.");
        }
        disponibilidad.setEstaReservado(true);
        disponibilidadRepositorio.save(disponibilidad);
    }

    /**
     * Libera un bloque de disponibilidad que estaba reservado.
     * Este método es para uso transaccional por otros servicios.
     * @param disponibilidad La entidad Disponibilidad a liberar.
     */
    @Transactional
    public void liberarDisponibilidad(Disponibilidad disponibilidad) {
        disponibilidad.setEstaReservado(false);
        disponibilidadRepositorio.save(disponibilidad);
    }
}
