package com.example.citasmedicas.mapeador;

import com.example.citasmedicas.dto.DisponibilidadDTO;
import com.example.citasmedicas.dto.CrearDisponibilidadDTO;
import com.example.citasmedicas.modelo.entidad.Disponibilidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapeador de MapStruct para convertir entre la entidad Disponibilidad
 * y los DTOs CrearDisponibilidadDTO y DisponibilidadDTO.
 */
@Mapper(componentModel = "spring")
public interface DisponibilidadMapeador {

    /**
     * Convierte una entidad Disponibilidad a un DisponibilidadDTO.
     * @param entidad La entidad Disponibilidad.
     * @return El DTO de Disponibilidad.
     */
    @Mapping(source = "doctor.id", target = "doctorId") // Mapea el ID del doctor
    DisponibilidadDTO aDTO(Disponibilidad entidad);

    /**
     * Convierte un CrearDisponibilidadDTO a una entidad Disponibilidad.
     * Se ignoran algunos campos que serán seteados por el servicio.
     * @param dto El DTO de creación de Disponibilidad.
     * @return La entidad Disponibilidad.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true) // El doctor se carga en el servicio
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "estaReservado", ignore = true) // Se inicializa por defecto en la entidad
    Disponibilidad aEntidad(CrearDisponibilidadDTO dto);

    /**
     * Convierte una lista de entidades Disponibilidad a una lista de DisponibilidadDTOs.
     * @param entidades La lista de entidades Disponibilidad.
     * @return La lista de DTOs de Disponibilidad.
     */
    List<DisponibilidadDTO> aListaDTO(List<Disponibilidad> entidades);
}

