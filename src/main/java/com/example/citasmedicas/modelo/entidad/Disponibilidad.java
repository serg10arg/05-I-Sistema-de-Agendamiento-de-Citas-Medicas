package com.example.citasmedicas.modelo.entidad;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import lombok.*;

import java.time.LocalDateTime; // Usar LocalDateTime para fecha y hora

/**
 * Entidad que representa un bloque de tiempo de disponibilidad de un doctor.
 * Extiende BaseEntidad para heredar campos de auditoría.
 */
@Entity
@Table(name = "disponibilidades")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Disponibilidad extends BaseEntidad {

    @ManyToOne(fetch = FetchType.LAZY) // Relación muchos a uno con Doctor
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor; // El doctor a quien pertenece esta disponibilidad

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio; // Hora de inicio del bloque de disponibilidad

    @Column(name = "hora_fin", nullable = false)
    private LocalDateTime horaFin; // Hora de fin del bloque de disponibilidad

    @Column(name = "esta_reservado", nullable = false)
    private Boolean estaReservado = false; // Indica si el bloque ya está reservado por una cita

}
