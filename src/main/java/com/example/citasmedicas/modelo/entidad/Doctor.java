package com.example.citasmedicas.modelo.entidad;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un profesional médico.
 * Extiende BaseEntidad para heredar campos de auditoría.
 */
@Entity
@Table(name = "doctores")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Doctor extends BaseEntidad {

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre; // Primer nombre del doctor

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido; // Apellido del doctor

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email; // Correo electrónico del doctor, usado para login

    @ToString.Exclude // Nunca incluir la contraseña en logs
    @Column(name = "contrasena", nullable = false)
    private String contrasena; // Contraseña codificada del doctor

    @Column(name = "url_foto_perfil", length = 255)
    private String urlFotoPerfil; // URL de la foto de perfil

    @ManyToOne(fetch = FetchType.LAZY) // Relación muchos a uno con Especialidad
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad; // Especialidad médica del doctor

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia; // Biografía del doctor

    @ToString.Exclude
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidad> disponibilidades = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();
}
