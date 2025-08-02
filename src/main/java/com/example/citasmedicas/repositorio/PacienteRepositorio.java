package com.example.citasmedicas.repositorio;


import com.example.citasmedicas.modelo.entidad.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad Paciente.
 * Proporciona operaciones CRUD y de búsqueda de Spring Data JPA.
 */
@Repository
public interface PacienteRepositorio extends JpaRepository<Paciente, UUID> {
    // Permite encontrar un paciente por su correo electrónico
    Optional<Paciente> findByEmail(String email);
}

