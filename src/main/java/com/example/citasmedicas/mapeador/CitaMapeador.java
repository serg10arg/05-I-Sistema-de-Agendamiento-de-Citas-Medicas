package com.example.citasmedicas.mapeador;

import com.example.citasmedicas.dto.CitaDTO;
import com.example.citasmedicas.dto.CrearCitaDTO;
import com.example.citasmedicas.modelo.entidad.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapeador de MapStruct para convertir entre la entidad Cita
 * y los DTOs CrearCitaDTO y CitaDTO.
 */
@Mapper(componentModel = "spring")
public interface CitaMapeador {

    /**
     * Convierte una entidad Cita a un CitaDTO.
     * @param entidad La entidad Cita.
     * @return El DTO de Cita.
     */
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "disponibilidad.id", target = "disponibilidadId")
    @Mapping(source = "disponibilidad.horaInicio", target = "horaInicio")
    @Mapping(source = "disponibilidad.horaFin", target = "horaFin")
    CitaDTO aDTO(Cita entidad);

    /**
     * Convierte un CrearCitaDTO a una entidad Cita.
     * Se ignoran campos que serán resueltos en el servicio (doctor, paciente, disponibilidad).
     * @param dto El DTO de creación de Cita.
     * @return La entidad Cita.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true) // Se carga en el servicio
    @Mapping(target = "paciente", ignore = true) // Se carga en el servicio
    @Mapping(target = "disponibilidad", ignore = true) // Se carga en el servicio
    @Mapping(target = "estado", ignore = true) // Se inicializa por defecto en la entidad
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Cita aEntidad(CrearCitaDTO dto);

    /**
     * Convierte una lista de entidades Cita a una lista de CitaDTOs.
     * @param entidades La lista de entidades Cita.
     * @return La lista de DTOs de Cita.
     */
    List<CitaDTO> aListaDTO(List<Cita> entidades);
}
