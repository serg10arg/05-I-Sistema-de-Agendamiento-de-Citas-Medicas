package com.example.citasmedicas.mapeador;

import com.example.citasmedicas.dto.EspecialidadDTO;
import com.example.citasmedicas.modelo.entidad.Especialidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapeador de MapStruct para convertir entre la entidad Especialidad y EspecialidadDTO.
 * Usa 'spring' como componente model para ser inyectable por Spring.
 */
@Mapper(componentModel = "spring")
public interface EspecialidadMapeador {

    /**
     * Convierte una entidad Especialidad a un EspecialidadDTO.
     * @param entidad La entidad Especialidad.
     * @return El DTO de Especialidad.
     */
    EspecialidadDTO aDTO(Especialidad entidad);

    /**
     * Convierte un EspecialidadDTO a una entidad Especialidad.
     * @param dto El DTO de Especialidad.
     * @return La entidad Especialidad.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Especialidad aEntidad(EspecialidadDTO dto);

    /**
     * Actualiza una entidad Especialidad existente a partir de un EspecialidadDTO.
     * @param dto El DTO con los datos de actualización.
     * @param entidad La entidad a actualizar (anotada con @MappingTarget).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void actualizarEntidad(EspecialidadDTO dto, @MappingTarget Especialidad entidad);

    /**
     * Convierte una lista de entidades Especialidad a una lista de EspecialidadDTOs.
     * @param entidades La lista de entidades Especialidad.
     * @return La lista de DTOs de Especialidad.
     */
    List<EspecialidadDTO> aListaDTO(List<Especialidad> entidades);
}
