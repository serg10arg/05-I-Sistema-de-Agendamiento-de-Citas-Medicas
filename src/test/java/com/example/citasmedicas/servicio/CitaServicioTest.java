package com.example.citasmedicas.servicio;

import com.example.citasmedicas.excepciones.AccesoDenegadoExcepcion;
import com.example.citasmedicas.excepciones.ConflictoHorarioExcepcion;
import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.excepciones.SolicitudInvalidaExcepcion;
import com.example.citasmedicas.dto.CitaDTO;
import com.example.citasmedicas.dto.CrearCitaDTO;
import com.example.citasmedicas.modelo.entidad.*;
import com.example.citasmedicas.mapeador.CitaMapeador;
import com.example.citasmedicas.repositorio.CitaRepositorio;
import com.example.citasmedicas.servicio.notificacion.EstrategiaNotificacion;
import com.example.citasmedicas.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*; // Importa todas las aserciones estáticas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*; // Importa todas las utilidades de Mockito

/**
 * Clase de pruebas unitarias para CitaServicio.
 * Verifica la lógica de negocio de agendamiento y cancelación de citas.
 */
@ExtendWith(MockitoExtension.class) // Habilita la integración de Mockito con JUnit 5
class CitaServicioTest {

    @Mock // Crea un mock para los repositorios y mapeadores
    private CitaRepositorio citaRepositorio;
    @Mock
    private DoctorServicio doctorServicio;
    @Mock
    private PacienteServicio pacienteServicio;
    @Mock
    private DisponibilidadServicio disponibilidadServicio;
    @Mock
    private CitaMapeador citaMapeador;
    @Mock
    private EstrategiaNotificacion estrategiaNotificacion; // Mock para la estrategia de notificación

    @InjectMocks // Inyecta los mocks en la instancia de CitaServicio
    private CitaServicio citaServicio;

    // Datos de prueba comunes
    private UUID doctorId;
    private UUID pacienteId;
    private UUID disponibilidadId;
    private UUID citaId;
    private Doctor doctor;
    private Paciente paciente;
    private Especialidad especialidad;
    private Disponibilidad disponibilidad;
    private Cita cita;
    private CrearCitaDTO crearCitaDTO;
    private CitaDTO citaDTO;

    @BeforeEach // Se ejecuta antes de cada método de prueba
    void setUp() {
        // Inicialización de IDs
        doctorId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        disponibilidadId = UUID.randomUUID();
        citaId = UUID.randomUUID();

        // Inicialización de entidades
        especialidad = Especialidad.builder()
                .id(UUID.randomUUID())
                .nombre("Cardiología")
                .build();

        doctor = TestDataBuilder.unDoctor()
                .especialidad(especialidad)
                .id(doctorId)
                .build();

        paciente = TestDataBuilder.unPaciente()
                .email("maria@example.com")
                .id(pacienteId)
                .build();


        LocalDateTime horaInicio = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime horaFin = horaInicio.plusMinutes(30);

        disponibilidad = TestDataBuilder.unaDisponibilidad()
                .doctor(doctor)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .estaReservado(false)
                .build();

        cita = Cita.builder()
                .id(citaId)
                .doctor(doctor)
                .paciente(paciente)
                .disponibilidad(disponibilidad)
                .razonVisita("Consulta de rutina")
                .estado(EstadoCita.CONFIRMADA)
                .build();

        // Inicialización de DTOs
        crearCitaDTO = new CrearCitaDTO(doctorId, pacienteId, disponibilidadId, "Consulta de rutina");

        citaDTO = new CitaDTO(citaId, doctorId, pacienteId, disponibilidadId,
                disponibilidad.getHoraInicio(), disponibilidad.getHoraFin(),
                EstadoCita.CONFIRMADA, "Consulta de rutina", cita.getFechaCreacion());
    }

    @Test
    @DisplayName("Debería agendar una cita cuando el horario está libre") //
    void test_shouldBookAppointment_whenSlotIsFree() {
        // Configurar mocks para agendar cita exitosamente
        when(doctorServicio.obtenerEntidadDoctorPorId(doctorId)).thenReturn(doctor);
        when(pacienteServicio.obtenerEntidadPacientePorId(pacienteId)).thenReturn(paciente);
        when(disponibilidadServicio.obtenerEntidadDisponibilidadDisponiblePorId(disponibilidadId)).thenReturn(disponibilidad);
        when(citaMapeador.aEntidad(crearCitaDTO)).thenReturn(cita);
        when(citaRepositorio.save(any(Cita.class))).thenReturn(cita);
        when(citaMapeador.aDTO(cita)).thenReturn(citaDTO);
        doNothing().when(disponibilidadServicio).reservarDisponibilidad(any(Disponibilidad.class));
        doNothing().when(estrategiaNotificacion).enviarNotificacion(anyString(), anyString(), anyString());

        // Ejecutar el método a probar
        CitaDTO resultado = citaServicio.agendarCita(crearCitaDTO);

        // Capturar el argumento para verificar su estado
        ArgumentCaptor<Cita> citaArgumentCaptor = ArgumentCaptor.forClass(Cita.class);

        // Aserciones
        assertNotNull(resultado);
        assertEquals(citaId, resultado.getId());
        assertEquals(EstadoCita.CONFIRMADA, resultado.getEstado());

        // Verificar interacciones de mocks
        verify(doctorServicio, times(1)).obtenerEntidadDoctorPorId(doctorId);
        verify(pacienteServicio, times(1)).obtenerEntidadPacientePorId(pacienteId);
        verify(disponibilidadServicio, times(1)).obtenerEntidadDisponibilidadDisponiblePorId(disponibilidadId);
        verify(disponibilidadServicio, times(1)).reservarDisponibilidad(disponibilidad);
        verify(citaRepositorio, times(1)).save(citaArgumentCaptor.capture());
        verify(estrategiaNotificacion, times(1)).enviarNotificacion(anyString(), anyString(), anyString());

        // Verificar el estado de la cita capturada
        assertEquals(EstadoCita.CONFIRMADA, citaArgumentCaptor.getValue().getEstado());
        assertEquals(doctor, citaArgumentCaptor.getValue().getDoctor());
    }

    @Test
    @DisplayName("No debería agendar una cita cuando el doctor no está disponible") //
    void test_shouldNotBookAppointment_whenDoctorIsNotAvailable() {
        // Configurar mocks para simular doctor no encontrado
        when(doctorServicio.obtenerEntidadDoctorPorId(doctorId)).thenThrow(new RecursoNoEncontradoExcepcion("Doctor no encontrado"));

        // Ejecutar y esperar excepción
        RecursoNoEncontradoExcepcion excepcion = assertThrows(RecursoNoEncontradoExcepcion.class, () ->
                citaServicio.agendarCita(crearCitaDTO)
        );

        // Aserciones
        assertEquals("Doctor no encontrado", excepcion.getMessage());
        // Verificar que no se llamaron métodos posteriores
        verify(pacienteServicio, never()).obtenerEntidadPacientePorId(any(UUID.class));
        verify(disponibilidadServicio, never()).obtenerEntidadDisponibilidadDisponiblePorId(any(UUID.class));
        verify(citaRepositorio, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("No debería agendar una cita cuando el horario ya está ocupado") //
    void test_shouldNotBookAppointment_whenTimeSlotIsAlreadyTaken() {
        // Configurar la disponibilidad como ya reservada
        when(doctorServicio.obtenerEntidadDoctorPorId(doctorId)).thenReturn(doctor);
        when(pacienteServicio.obtenerEntidadPacientePorId(pacienteId)).thenReturn(paciente);
        // Simular que la disponibilidad no se encuentra porque ya está reservada
        when(disponibilidadServicio.obtenerEntidadDisponibilidadDisponiblePorId(disponibilidadId)).thenThrow(new RecursoNoEncontradoExcepcion("Bloque de disponibilidad no encontrado o ya reservado"));

        // Ejecutar y esperar excepción de conflicto de horario
        ConflictoHorarioExcepcion excepcion = assertThrows(ConflictoHorarioExcepcion.class, () ->
                citaServicio.agendarCita(crearCitaDTO)
        );

        // Aserciones
        // La excepción ahora viene de la validación interna de la disponibilidad
        assertEquals("El horario seleccionado ya no se encuentra disponible para el doctor especificado.", excepcion.getMessage()); // Ajustar si el mensaje cambia
        verify(disponibilidadServicio, never()).reservarDisponibilidad(any(Disponibilidad.class)); // No debería intentar reservar
        verify(citaRepositorio, never()).save(any(Cita.class)); // No debería intentar guardar la cita
    }

    @Test
    @DisplayName("Debería lanzar RecursoNoEncontradoExcepcion si la disponibilidad no existe")
    void test_shouldThrowException_whenAvailabilitySlotNotFound() {
        // Configurar mocks
        when(doctorServicio.obtenerEntidadDoctorPorId(doctorId)).thenReturn(doctor);
        when(pacienteServicio.obtenerEntidadPacientePorId(pacienteId)).thenReturn(paciente);
        when(disponibilidadServicio.obtenerEntidadDisponibilidadDisponiblePorId(disponibilidadId))
                .thenThrow(new RecursoNoEncontradoExcepcion("Bloque de disponibilidad no encontrado"));

        // Ejecutar y esperar la excepción
        RecursoNoEncontradoExcepcion excepcion = assertThrows(RecursoNoEncontradoExcepcion.class, () ->
                citaServicio.agendarCita(crearCitaDTO)
        );

        // Aserciones
        assertEquals("Bloque de disponibilidad no encontrado", excepcion.getMessage());
        verify(citaRepositorio, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("No debería agendar una cita cuando la disponibilidad no pertenece al doctor")
    void test_shouldNotBookAppointment_whenAvailabilityDoesNotBelongToDoctor() {
        // Simular que la disponibilidad pertenece a otro doctor
        Doctor otroDoctor = TestDataBuilder.unDoctor().build(); // Usando el builder
        disponibilidad.setDoctor(otroDoctor);

        when(doctorServicio.obtenerEntidadDoctorPorId(doctorId)).thenReturn(doctor); // Se pide el doctor original
        when(pacienteServicio.obtenerEntidadPacientePorId(pacienteId)).thenReturn(paciente);
        // El servicio de disponibilidad devuelve la entidad, la validación está en CitaServicio
        when(disponibilidadServicio.obtenerEntidadDisponibilidadDisponiblePorId(disponibilidadId)).thenReturn(disponibilidad);

        // Ejecutar y esperar excepción de conflicto
        ConflictoHorarioExcepcion excepcion = assertThrows(ConflictoHorarioExcepcion.class, () ->
                citaServicio.agendarCita(crearCitaDTO)
        );

        // Aserciones
        assertEquals("El horario seleccionado ya no se encuentra disponible para el doctor especificado.", excepcion.getMessage());
        verify(disponibilidadServicio, never()).reservarDisponibilidad(any(Disponibilidad.class));
        verify(citaRepositorio, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debería cancelar una cita con al menos 24 horas de antelación")
    void test_shouldCancelAppointment_whenMoreThan24HoursNotice() {
        // Asegurar que la cita sea en el futuro, más de 24 horas
        LocalDateTime horaInicioCitaFutura = LocalDateTime.now().plusDays(2).plusHours(1);
        disponibilidad.setHoraInicio(horaInicioCitaFutura);
        cita.setEstado(EstadoCita.CONFIRMADA); // Asegurar que el estado inicial es CONFIRMADA
        disponibilidad.setEstaReservado(true); // Asegurar que la disponibilidad está reservada

        when(citaRepositorio.findById(citaId)).thenReturn(Optional.of(cita));
        when(citaRepositorio.save(any(Cita.class))).thenReturn(cita);
        doNothing().when(disponibilidadServicio).liberarDisponibilidad(any(Disponibilidad.class));
        when(citaMapeador.aDTO(cita)).thenReturn(citaDTO); // Mockear el mapeo de retorno
        doNothing().when(estrategiaNotificacion).enviarNotificacion(anyString(), anyString(), anyString());

        // Ejecutar
        CitaDTO resultado = citaServicio.cancelarCita(citaId);

        // Aserciones
        assertNotNull(resultado);
        assertEquals(EstadoCita.CANCELADA, cita.getEstado()); // Verificar que el estado cambió a CANCELADA
        assertFalse(disponibilidad.getEstaReservado()); // Verificar que la disponibilidad se liberó
        verify(citaRepositorio, times(1)).save(cita);
        verify(disponibilidadServicio, times(1)).liberarDisponibilidad(disponibilidad);
        verify(estrategiaNotificacion, times(1)).enviarNotificacion(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("No debería cancelar una cita con menos de 24 horas de antelación") //
    void test_shouldNotCancelAppointment_whenLessThan24HoursNotice() {
        // Configurar la cita para que sea en menos de 24 horas
        LocalDateTime horaInicioCitaProxima = LocalDateTime.now().plusHours(12); // 12 horas en el futuro
        disponibilidad.setHoraInicio(horaInicioCitaProxima);
        cita.setEstado(EstadoCita.CONFIRMADA); // Asegurar que el estado inicial es CONFIRMADA
        disponibilidad.setEstaReservado(true);

        when(citaRepositorio.findById(citaId)).thenReturn(Optional.of(cita));

        // Ejecutar y esperar excepción de acceso denegado
        AccesoDenegadoExcepcion excepcion = assertThrows(AccesoDenegadoExcepcion.class, () ->
                citaServicio.cancelarCita(citaId)
        );

        // Aserciones
        assertEquals("La cita solo puede ser cancelada con al menos 24 horas de antelación.", excepcion.getMessage());
        assertEquals("APPOINTMENT_CANCELLATION_WINDOW_CLOSED", excepcion.getCodigoError()); // Código de error específico
        assertEquals(EstadoCita.CONFIRMADA, cita.getEstado()); // El estado no debería haber cambiado
        assertTrue(disponibilidad.getEstaReservado()); // La disponibilidad no debería haberse liberado

        verify(citaRepositorio, never()).save(any(Cita.class)); // No debería intentar guardar la cita
        verify(disponibilidadServicio, never()).liberarDisponibilidad(any(Disponibilidad.class)); // Ni la disponibilidad
        verify(estrategiaNotificacion, never()).enviarNotificacion(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("No debería cancelar una cita que ya está cancelada")
    void test_shouldNotCancelAppointment_whenAlreadyCancelled() {
        cita.setEstado(EstadoCita.CANCELADA); // Cita ya está cancelada
        when(citaRepositorio.findById(citaId)).thenReturn(Optional.of(cita));

        // Ejecutar y esperar excepción de solicitud inválida
        SolicitudInvalidaExcepcion excepcion = assertThrows(SolicitudInvalidaExcepcion.class, () ->
                citaServicio.cancelarCita(citaId)
        );

        // Aserciones
        assertEquals("La cita ya está cancelada.", excepcion.getMessage());
        verify(citaRepositorio, never()).save(any(Cita.class));
        verify(disponibilidadServicio, never()).liberarDisponibilidad(any(Disponibilidad.class));
        verify(estrategiaNotificacion, never()).enviarNotificacion(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debería obtener citas por paciente")
    void test_obtenerCitasPorPaciente() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Cita> paginaEntidades = new PageImpl<>(Arrays.asList(cita), pageable, 1);
        Page<CitaDTO> paginaDTOs = new PageImpl<>(Arrays.asList(citaDTO), pageable, 1);

        when(pacienteServicio.obtenerEntidadPacientePorId(pacienteId)).thenReturn(paciente);
        when(citaRepositorio.findByPaciente(paciente, pageable)).thenReturn(paginaEntidades);
        when(citaMapeador.aDTO(any(Cita.class))).thenReturn(citaDTO);

        Page<CitaDTO> resultado = citaServicio.obtenerCitasPorPaciente(pacienteId, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals(citaDTO.getId(), resultado.getContent().get(0).getId());
        verify(pacienteServicio, times(1)).obtenerEntidadPacientePorId(pacienteId);
        verify(citaRepositorio, times(1)).findByPaciente(paciente, pageable);
    }

    @Test
    @DisplayName("Debería obtener citas por doctor")
    void test_obtenerCitasPorDoctor() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Cita> paginaEntidades = new PageImpl<>(Arrays.asList(cita), pageable, 1);
        Page<CitaDTO> paginaDTOs = new PageImpl<>(Arrays.asList(citaDTO), pageable, 1);

        when(doctorServicio.obtenerEntidadDoctorPorId(doctorId)).thenReturn(doctor);
        when(citaRepositorio.findByDoctorOrderByDisponibilidadHoraInicioAsc(doctor, pageable)).thenReturn(paginaEntidades);
        when(citaMapeador.aDTO(any(Cita.class))).thenReturn(citaDTO);

        Page<CitaDTO> resultado = citaServicio.obtenerCitasPorDoctor(doctorId, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals(citaDTO.getId(), resultado.getContent().get(0).getId());
        verify(doctorServicio, times(1)).obtenerEntidadDoctorPorId(doctorId);
        verify(citaRepositorio, times(1)).findByDoctorOrderByDisponibilidadHoraInicioAsc(doctor, pageable);
    }

    @Test
    @DisplayName("Debería obtener una cita por ID")
    void test_obtenerCitaPorId() {
        when(citaRepositorio.findById(citaId)).thenReturn(Optional.of(cita));
        when(citaMapeador.aDTO(cita)).thenReturn(citaDTO);

        CitaDTO resultado = citaServicio.obtenerCitaPorId(citaId);

        assertNotNull(resultado);
        assertEquals(citaId, resultado.getId());
        verify(citaRepositorio, times(1)).findById(citaId);
    }

    @Test
    @DisplayName("Debería lanzar RecursoNoEncontradoExcepcion al obtener cita por ID inexistente")
    void test_obtenerCitaPorId_notFound() {
        when(citaRepositorio.findById(citaId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoExcepcion.class, () ->
                citaServicio.obtenerCitaPorId(citaId)
        );
        verify(citaRepositorio, times(1)).findById(citaId);
        verify(citaMapeador, never()).aDTO(any(Cita.class));
    }
}
