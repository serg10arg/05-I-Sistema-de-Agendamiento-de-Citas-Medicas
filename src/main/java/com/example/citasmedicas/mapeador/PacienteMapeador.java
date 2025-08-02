package com.example.citasmedicas.mapeador;

import com.example.citasmedicas.dto.PacienteDTO;
import com.example.citasmedicas.modelo.entidad.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapeador de MapStruct para convertir entre la entidad Paciente y PacienteDTO.
 */
@Mapper(componentModel = "spring")
public interface PacienteMapeador {

    /**
     * Convierte una entidad Paciente a un PacienteDTO.
     * @param entidad La entidad Paciente.
     * @return El DTO de Paciente.
     */
    PacienteDTO aDTO(Paciente entidad);

    /**
     * Convierte un PacienteDTO a una entidad Paciente.
     * @param dto El DTO de Paciente.
     * @return La entidad Paciente.
     */
    @Mapping(target = "id", ignore = true) // Ignorar ID al crear/actualizar desde DTO
    @Mapping(target = "citas", ignore = true) // No gestionar la colección de citas desde este mapeo
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Paciente aEntidad(PacienteDTO dto);

    /**
     * Actualiza una entidad Paciente existente a partir de un PacienteDTO.
     * @param dto El DTO con los datos de actualización.
     * @param entidad La entidad a actualizar (anotada con @MappingTarget).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void actualizarEntidad(PacienteDTO dto, @MappingTarget Paciente entidad);

    /**
     * Convierte una lista de entidades Paciente a una lista de PacienteDTOs.
     * @param entidades La lista de entidades Paciente.
     * @return La lista de DTOs de Paciente.
     */
    List<PacienteDTO> aListaDTO(List<Paciente> entidades);
}
