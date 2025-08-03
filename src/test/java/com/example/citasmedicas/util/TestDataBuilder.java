package com.example.citasmedicas.util;

import com.example.citasmedicas.modelo.entidad.Disponibilidad;
import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Paciente;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase de utilidad para construir entidades de prueba con el patrón Builder.
 * Facilita la creación de datos de prueba legibles y mantenibles.
 */
public class TestDataBuilder {

    public static Doctor.DoctorBuilder<?, ?> unDoctor() {
        return Doctor.builder()
                .id(UUID.randomUUID())
                .primerNombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .contrasena("password");
    }

    public static Paciente.PacienteBuilder<?, ?> unPaciente() {
        return Paciente.builder()
                .id(UUID.randomUUID())
                .primerNombre("Maria")
                .apellido("Gomez")
                .email("maria.gomez@example.com")
                .contrasena("password");
    }

    public static Disponibilidad.DisponibilidadBuilder<?, ?> unaDisponibilidad() {
        return Disponibilidad.builder()
                .id(UUID.randomUUID())
                .horaInicio(LocalDateTime.now().plusDays(2))
                .horaFin(LocalDateTime.now().plusDays(2).plusMinutes(30))
                .estaReservado(false);
    }

    // Nota: Para que esto funcione, las entidades Doctor y Paciente deben tener la anotación @Builder de Lombok.
    // Ejemplo para Doctor.java:
    // @Builder
    // @Entity
    // public class Doctor extends BaseEntidad { ... }

}