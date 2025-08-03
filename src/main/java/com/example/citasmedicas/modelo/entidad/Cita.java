package com.example.citasmedicas.modelo.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.*; // Keep existing
import lombok.experimental.SuperBuilder; // Add this

/**
 * Entidad que representa una cita agendada entre un doctor y un paciente.
 * Extiende BaseEntidad para heredar campos de auditoría.
 */
@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Cita extends BaseEntidad {

    @ManyToOne(fetch = FetchType.LAZY) // Relación muchos a uno con Doctor
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor; // Doctor de la cita

    @ManyToOne(fetch = FetchType.LAZY) // Relación muchos a uno con Paciente
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente; // Paciente de la cita

    @ManyToOne(fetch = FetchType.LAZY) // Relación muchos a uno con Disponibilidad
    @JoinColumn(name = "disponibilidad_id", nullable = false, unique = true)
    private Disponibilidad disponibilidad; // El bloque de disponibilidad asociado a esta cita

    @Column(name = "razon_visita", length = 500)
    private String razonVisita; // Razón de la visita

    @Enumerated(EnumType.STRING) // Almacena el enum como String en la BD
    @Column(name = "estado", nullable = false, length = 50)
    private EstadoCita estado = EstadoCita.CONFIRMADA; // Estado actual de la cita (por defecto CONFIRMADA)
}
