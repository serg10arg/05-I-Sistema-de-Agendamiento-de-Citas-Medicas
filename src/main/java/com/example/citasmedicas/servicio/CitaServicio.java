package com.example.citasmedicas.servicio;

import com.example.citasmedicas.excepciones.AccesoDenegadoExcepcion;
import com.example.citasmedicas.excepciones.ConflictoHorarioExcepcion;
import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.excepciones.SolicitudInvalidaExcepcion;
import com.example.citasmedicas.dto.CitaDTO;
import com.example.citasmedicas.dto.CrearCitaDTO;
import com.example.citasmedicas.modelo.entidad.Cita;
import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Disponibilidad;
import com.example.citasmedicas.modelo.entidad.EstadoCita;
import com.example.citasmedicas.modelo.entidad.Paciente;

import com.example.citasmedicas.mapeador.CitaMapeador;
import com.example.citasmedicas.repositorio.CitaRepositorio;
import com.example.citasmedicas.servicio.notificacion.EstrategiaNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la gestión de Citas.
 * Contiene la lógica de negocio principal para agendar, cancelar y consultar citas.
 */
@Service
public class CitaServicio {

    private final CitaRepositorio citaRepositorio;
    private final DoctorServicio doctorServicio;
    private final PacienteServicio pacienteServicio;
    private final DisponibilidadServicio disponibilidadServicio;
    private final CitaMapeador citaMapeador;
    private final EstrategiaNotificacion estrategiaNotificacion; // Inyección de la estrategia de notificación

    public CitaServicio(CitaRepositorio citaRepositorio, DoctorServicio doctorServicio, PacienteServicio pacienteServicio, DisponibilidadServicio disponibilidadServicio, CitaMapeador citaMapeador, EstrategiaNotificacion estrategiaNotificacion) {
        this.citaRepositorio = citaRepositorio;
        this.doctorServicio = doctorServicio;
        this.pacienteServicio = pacienteServicio;
        this.disponibilidadServicio = disponibilidadServicio;
        this.citaMapeador = citaMapeador;
        this.estrategiaNotificacion = estrategiaNotificacion;
    }

    /**
     * Agenda una nueva cita para un paciente con un doctor en un horario específico.
     * Implementa las validaciones de negocio críticas.
     * @param dto El DTO con los detalles de la cita.
     * @return El DTO de la cita creada.
     * @throws RecursoNoEncontradoExcepcion Si el doctor, paciente o disponibilidad no existen.
     * @throws ConflictoHorarioExcepcion Si el bloque de disponibilidad ya está reservado o no pertenece al doctor.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE) // Asegura atomicitad para la reserva de horario
    public CitaDTO agendarCita(CrearCitaDTO dto) {
        // 1. Validar que doctor y paciente existan
        Doctor doctor = doctorServicio.obtenerEntidadDoctorPorId(dto.getDoctorId());
        Paciente paciente = pacienteServicio.obtenerEntidadPacientePorId(dto.getPacienteId());

        // 2. Validar que el bloque de disponibilidad exista y esté disponible (no reservado)
        Disponibilidad disponibilidad = disponibilidadServicio.obtenerEntidadDisponibilidadDisponiblePorId(dto.getDisponibilidadId());


        // Validar que el bloque no haya sido reservado por otra cita en el instante de la transacción.
        // y que pertenezca al doctorId especificado.
        if (disponibilidad.getEstaReservado() || !disponibilidad.getDoctor().getId().equals(doctor.getId())) {
            throw new ConflictoHorarioExcepcion("El horario seleccionado ya no se encuentra disponible para el doctor especificado."); //
        }

        // 3. Marcar el bloque de disponibilidad como reservado
        disponibilidadServicio.reservarDisponibilidad(disponibilidad);

        // 4. Crear la cita
        Cita cita = citaMapeador.aEntidad(dto);
        cita.setDoctor(doctor);
        cita.setPaciente(paciente);
        cita.setDisponibilidad(disponibilidad);

        Cita citaGuardada = citaRepositorio.save(cita);

        // 5. Notificar confirmación de cita (ejemplo de patrón Strategy)
        estrategiaNotificacion.enviarNotificacion(
                paciente.getEmail(),
                "Cita Confirmada",
                "Su cita con el Dr./Dra. " + doctor.getPrimerNombre() + " " + doctor.getApellido() +
                        " ha sido confirmada para el " + disponibilidad.getHoraInicio() +
                        " por la razón: " + cita.getRazonVisita()
        );

        return citaMapeador.aDTO(citaGuardada);
    }

    /**
     * Cancela una cita existente.
     * Aplica la política de cancelación de 24 horas.
     * @param idCita El UUID de la cita a cancelar.
     * @return El DTO de la cita actualizada.
     * @throws RecursoNoEncontradoExcepcion Si la cita no existe.
     * @throws SolicitudInvalidaExcepcion Si la cita ya está cancelada o finalizada.
     * @throws AccesoDenegadoExcepcion Si se intenta cancelar con menos de 24 horas de antelación.
     */
    @Transactional
    public CitaDTO cancelarCita(UUID idCita) {
        Cita cita = citaRepositorio.findById(idCita)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cita no encontrada con ID: " + idCita));

        // Validar estado actual de la cita
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.FINALIZADA) {
            throw new SolicitudInvalidaExcepcion("La cita ya está " + cita.getEstado().name().toLowerCase() + ".");
        }

        // Política de cancelación: 24 horas de antelación
        LocalDateTime horaActual = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime horaInicioCita = cita.getDisponibilidad().getHoraInicio();
        long horasHastaCita = ChronoUnit.HOURS.between(horaActual, horaInicioCita);

        if (horasHastaCita < 24) {
            throw new AccesoDenegadoExcepcion("APPOINTMENT_CANCELLATION_WINDOW_CLOSED", // Código de error específico
                    "La cita solo puede ser cancelada con al menos 24 horas de antelación.", // Mensaje
                    "Intento de cancelar una cita con menos de 24 horas de antelación. Horas restantes: " + horasHastaCita); // Detalles
        }

        cita.setEstado(EstadoCita.CANCELADA); // Cambia el estado a CANCELADA
        // Liberar el bloque de disponibilidad
        disponibilidadServicio.liberarDisponibilidad(cita.getDisponibilidad());

        Cita citaActualizada = citaRepositorio.save(cita);

        // Notificar cancelación
        estrategiaNotificacion.enviarNotificacion(
                cita.getPaciente().getEmail(),
                "Cita Cancelada",
                "Su cita con el Dr./Dra. " + cita.getDoctor().getPrimerNombre() + " " + cita.getDoctor().getApellido() +
                        " para el " + cita.getDisponibilidad().getHoraInicio() + " ha sido cancelada."
        );

        return citaMapeador.aDTO(citaActualizada);
    }

    /**
     * Consulta las citas de un paciente con paginación.
     * @param pacienteId El UUID del paciente.
     * @param pagina El número de página (base 0).
     * @param tamano El tamaño de la página.
     * @return Una Page de DTOs de citas.
     * @throws RecursoNoEncontradoExcepcion Si el paciente no existe.
     */
    @Transactional(readOnly = true)
    public Page<CitaDTO> obtenerCitasPorPaciente(UUID pacienteId, int pagina, int tamano) {
        Paciente paciente = pacienteServicio.obtenerEntidadPacientePorId(pacienteId);
        Pageable paginacion = PageRequest.of(pagina, tamano);
        Page<Cita> citas = citaRepositorio.findByPaciente(paciente, paginacion); //
        return citas.map(citaMapeador::aDTO);
    }

    /**
     * Consulta las citas de un doctor con paginación.
     * @param doctorId El UUID del doctor.
     * @param pagina El número de página (base 0).
     * @param tamano El tamaño de la página.
     * @return Una Page de DTOs de citas.
     * @throws RecursoNoEncontradoExcepcion Si el doctor no existe.
     */
    @Transactional(readOnly = true)
    public Page<CitaDTO> obtenerCitasPorDoctor(UUID doctorId, int pagina, int tamano) {
        Doctor doctor = doctorServicio.obtenerEntidadDoctorPorId(doctorId);
        Pageable paginacion = PageRequest.of(pagina, tamano);
        Page<Cita> citas = citaRepositorio.findByDoctorOrderByDisponibilidadHoraInicioAsc(doctor, paginacion); //
        return citas.map(citaMapeador::aDTO);
    }

    /**
     * Obtiene una cita específica por su ID.
     * @param idCita El UUID de la cita.
     * @return El DTO de la cita.
     * @throws RecursoNoEncontradoExcepcion Si la cita no existe.
     */
    @Transactional(readOnly = true)
    public CitaDTO obtenerCitaPorId(UUID idCita) {
        Cita cita = citaRepositorio.findById(idCita)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cita no encontrada con ID: " + idCita));
        return citaMapeador.aDTO(cita);
    }
}
