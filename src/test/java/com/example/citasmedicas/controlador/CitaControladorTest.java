package com.example.citasmedicas.controlador;

import com.example.citasmedicas.dto.CitaDTO;
import com.example.citasmedicas.dto.CrearCitaDTO;
import com.example.citasmedicas.modelo.entidad.EstadoCita;
import com.example.citasmedicas.seguridad.config.JwtPropiedades;
import com.example.citasmedicas.seguridad.servicio.JwtServicio;
import com.example.citasmedicas.servicio.CitaServicio;
import com.example.citasmedicas.servicio.DisponibilidadServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CitaControlador.class) // Carga solo el contexto web para este controlador
@Import({JwtServicio.class, JwtPropiedades.class}) // Importar dependencias de seguridad necesarias
class CitaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock // Mockea el servicio para aislar el controlador
    private CitaServicio citaServicio;

    @Mock
    private DisponibilidadServicio disponibilidadServicio;

    @Test
    @WithMockUser(authorities = "PATIENT") // Simula un usuario autenticado con el rol PATIENT
    void agendarCita_deberiaRetornar201_cuandoLaCitaEsValida() throws Exception {
        // Arrange
        UUID doctorId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID disponibilidadId = UUID.randomUUID();
        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(doctorId, pacienteId, disponibilidadId, "Consulta");

        CitaDTO citaCreadaDTO = new CitaDTO(UUID.randomUUID(), doctorId, pacienteId, disponibilidadId,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), EstadoCita.CONFIRMADA, "Consulta", null);

        when(citaServicio.agendarCita(any(CrearCitaDTO.class))).thenReturn(citaCreadaDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearCitaDTO)))
                .andExpect(status().isCreated()) // Verificar que el estado es 201 Created
                .andExpect(jsonPath("$.estado").value("CONFIRMADA")); // Verificar parte del cuerpo de la respuesta
    }
}