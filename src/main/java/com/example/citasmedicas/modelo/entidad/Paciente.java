package com.example.citasmedicas.modelo.entidad;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un paciente.
 * Extiende BaseEntidad para heredar campos de auditoría.
 */
@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Paciente extends BaseEntidad {

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre; // Primer nombre del paciente

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido; // Apellido del paciente

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email; // Correo electrónico del paciente

    @Column(name = "telefono", length = 20)
    private String telefono; // Número de teléfono del paciente

    @ToString.Exclude
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();
}
