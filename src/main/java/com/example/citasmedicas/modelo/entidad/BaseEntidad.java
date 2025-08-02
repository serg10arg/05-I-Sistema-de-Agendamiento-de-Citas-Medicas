package com.example.citasmedicas.modelo.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID; // UUID para identificadores

/**
 * Clase base para todas las entidades, proporcionando campos comunes de auditoría.
 * - id: Identificador único universal (UUID).
 * - fechaCreacion: Marca de tiempo de cuándo se creó la entidad.
 * - fechaActualizacion: Marca de tiempo de cuándo se actualizó la entidad por última vez.
 */
@Getter
@Setter
@MappedSuperclass // No es una entidad por sí misma, sino una superclase mapeada
@EntityListeners(AuditingEntityListener.class) // Habilita la auditoría automática
public abstract class BaseEntidad {

    @Id // Marca como clave primaria
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @CreatedDate // Anotación de Spring Data JPA para la fecha de creación
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @LastModifiedDate // Anotación de Spring Data JPA para la fecha de última modificación
    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

}
