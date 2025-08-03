package com.example.citasmedicas.modelo.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa una especialidad médica.
 * Extiende BaseEntidad para heredar campos de auditoría.
 */
@Entity
@Table(name = "especialidades")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Especialidad extends BaseEntidad {

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre; // Nombre de la especialidad (ej. "Cardiología")
}
