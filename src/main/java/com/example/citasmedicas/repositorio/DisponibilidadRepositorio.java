package com.example.citasmedicas.repositorio;


import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad Disponibilidad.
 * Proporciona operaciones CRUD y de búsqueda de Spring Data JPA.
 */
@Repository
public interface DisponibilidadRepositorio extends JpaRepository<Disponibilidad, UUID> {
    // Encuentra bloques de disponibilidad para un doctor en un rango de fechas
    List<Disponibilidad> findByDoctorAndHoraInicioBetweenOrderByHoraInicioAsc(Doctor doctor, LocalDateTime horaInicio, LocalDateTime horaFin);

    // Encuentra un bloque de disponibilidad específico para un doctor y una hora, no reservado
    Optional<Disponibilidad> findByDoctorAndHoraInicioAndEstaReservadoFalse(Doctor doctor, LocalDateTime horaInicio);

    // Encuentra bloques de disponibilidad que se superponen para un doctor
    List<Disponibilidad> findByDoctorAndHoraFinAfterAndHoraInicioBefore(Doctor doctor, LocalDateTime horaInicio, LocalDateTime horaFin);

    // Encuentra un bloque de disponibilidad por su ID y si no está reservado
    Optional<Disponibilidad> findByIdAndEstaReservadoFalse(UUID id);

    // Cuenta los bloques de disponibilidad no reservados para un doctor en un rango de fechas
    @Query("SELECT COUNT(d) FROM Disponibilidad d WHERE d.doctor = :doctor AND d.estaReservado = false AND d.horaInicio BETWEEN :inicio AND :fin")
    long countDisponibilidadNoReservada(Doctor doctor, LocalDateTime inicio, LocalDateTime fin);
}
