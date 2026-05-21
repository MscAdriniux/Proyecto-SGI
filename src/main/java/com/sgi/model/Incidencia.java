package com.sgi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIncidencia;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private String estado; // Pendiente, En Proceso, Resuelta

    @Column(nullable = false)
    private String prioridad; // Alta, Media, Baja

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // --- Constructor vacío requerido por Spring ---
    public Incidencia() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "Pendiente";
    }

    // --- Getters y Setters ---
    public Long getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(Long idIncidencia) { this.idIncidencia = idIncidencia; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}