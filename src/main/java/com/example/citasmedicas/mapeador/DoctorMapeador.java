package com.example.citasmedicas.mapeador;

import com.example.citasmedicas.dto.DoctorDTO;
import com.example.citasmedicas.modelo.entidad.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapeador de MapStruct para convertir entre la entidad Doctor y DoctorDTO.
 * Depende de EspecialidadMapeador para la conversión de especialidades.
 */
@Mapper(componentModel = "spring", uses = { EspecialidadMapeador.class })
public interface DoctorMapeador {

    /**
     * Convierte una entidad Doctor a un DoctorDTO.
     * El mapeo de la especialidad se delega a EspecialidadMapeador.
     * @param entidad La entidad Doctor.
     * @return El DTO de Doctor.
     */
    DoctorDTO aDTO(Doctor entidad);

    /**
     * Convierte un DoctorDTO a una entidad Doctor.
     * El mapeo de la especialidad se delega a EspecialidadMapeador.
     * @param dto El DTO de Doctor.
     * @return La entidad Doctor.
     */
    @Mapping(target = "id", ignore = true) // Ignorar ID al crear/actualizar desde DTO, se maneja por el servicio
    @Mapping(target = "disponibilidades", ignore = true) // Las colecciones se gestionan por separado
    @Mapping(target = "citas", ignore = true) // Las colecciones se gestionan por separado
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Doctor aEntidad(DoctorDTO dto);

    /**
     * Actualiza una entidad Doctor existente a partir de un DoctorDTO.
     * @param dto El DTO con los datos de actualización.
     * @param entidad La entidad a actualizar (anotada con @MappingTarget).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponibilidades", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void actualizarEntidad(DoctorDTO dto, @MappingTarget Doctor entidad);

    /**
     * Convierte una lista de entidades Doctor a una lista de DoctorDTOs.
     * @param entidades La lista de entidades Doctor.
     * @return La lista de DTOs de Doctor.
     */
    List<DoctorDTO> aListaDTO(List<Doctor> entidades);
}
