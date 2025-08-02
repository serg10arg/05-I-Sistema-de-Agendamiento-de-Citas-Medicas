package com.example.citasmedicas.dto;


import java.util.UUID;

/**
 * DTO para representar la información esencial de un doctor en una vista de lista.
 * @param id El ID del doctor.
 * @param nombreCompleto El nombre completo del doctor.
 * @param especialidadNombre El nombre de la especialidad del doctor.
 * @param urlFotoPerfil La URL de la foto de perfil del doctor.
 */
public record DoctorListViewDTO(
        UUID id,
        String nombreCompleto,
        String especialidadNombre,
        String urlFotoPerfil
) {}