package com.example.citasmedicas.repositorio;


import com.example.citasmedicas.modelo.entidad.Cita;
import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Paciente;
import com.example.citasmedicas.modelo.entidad.EstadoCita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad Cita.
 * Proporciona operaciones CRUD y de búsqueda de Spring Data JPA.
 */
@Repository
public interface CitaRepositorio extends JpaRepository<Cita, UUID> {
    // Encuentra todas las citas de un paciente ordenadas por hora de inicio
    @Query("""
            SELECT c FROM Cita c
            JOIN c.disponibilidad d
            WHERE c.paciente = :paciente
            ORDER BY d.horaInicio ASC
            """)
    Page<Cita> findByPaciente(Paciente paciente, Pageable pageable);

    // Encuentra todas las citas de un doctor ordenadas por hora de inicio
    Page<Cita> findByDoctorOrderByDisponibilidadHoraInicioAsc(Doctor doctor, Pageable pageable);

    // Encuentra citas confirmadas de un doctor en un rango de tiempo
    List<Cita> findByDoctorAndEstadoAndDisponibilidadHoraInicioBetween(Doctor doctor, EstadoCita estado, LocalDateTime inicio, LocalDateTime fin);
}
