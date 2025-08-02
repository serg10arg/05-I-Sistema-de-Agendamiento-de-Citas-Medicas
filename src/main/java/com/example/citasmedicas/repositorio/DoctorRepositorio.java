package com.example.citasmedicas.repositorio;

import com.example.citasmedicas.dto.DoctorListViewDTO;
import com.example.citasmedicas.modelo.entidad.Doctor;
import com.example.citasmedicas.modelo.entidad.Especialidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad Doctor.
 * Proporciona operaciones CRUD y de búsqueda de Spring Data JPA.
 */
@Repository
public interface DoctorRepositorio extends JpaRepository<Doctor, UUID> {
    // Permite encontrar doctores por especialidad, con paginación
    @Query("""
            SELECT new com.example.citasmedicas.dto.DoctorListViewDTO(
                d.id,
                CONCAT(d.primerNombre, ' ', d.apellido),
                e.nombre,
                d.urlFotoPerfil
            )
            FROM Doctor d JOIN d.especialidad e
            WHERE e = :especialidad
            """)
    Page<DoctorListViewDTO> findByEspecialidad(Especialidad especialidad, Pageable pageable);

    // Permite encontrar doctores por primer nombre o apellido, ignorando mayúsculas/minúsculas
    Page<Doctor> findByPrimerNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String primerNombre, String apellido, Pageable pageable);
}
