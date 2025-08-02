package com.example.citasmedicas.repositorio;

import com.example.citasmedicas.modelo.entidad.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad Especialidad.
 * Proporciona operaciones CRUD y de búsqueda de Spring Data JPA.
 */
@Repository
public interface EspecialidadRepositorio extends JpaRepository<Especialidad, UUID> {
    // Permite encontrar una especialidad por su nombre
    Optional<Especialidad> findByNombre(String nombre);
}

