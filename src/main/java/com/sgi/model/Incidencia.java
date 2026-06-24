package com.sgi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un ticket o reporte de incidencia en el sistema.
 * Mapea directamente con la tabla "incidencia" en la base de datos MySQL.
 */
@Entity
@Table(name = "incidencia")
public class Incidencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idIncidencia;

    @Column(nullable = false, length = 50)
    private String estado = "PENDIENTE";

    @Column(nullable = false, length = 50)
    private String prioridad;
  
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(length = 300)
    private String comentarioAdmin;

    @Column(length = 100)
    private String categoria;

    @Column(length = 150)
    private String ubicacion;

    @Column(name = "asignado_a", length = 100)
    private String asignadoA;
    
    @Column(name = "tipo_incidencia", nullable = false, length = 200)
    private String tipoIncidencia;
    
    @Column(name = "evidencia_url")
    private String evidenciaUrl;

    @Column(name = "resolucion", length = 300)
    private String resolucion;

    // Relación: Muchas incidencias pertenecen a un solo usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // ==========================================
    // Constructores, Getters y Setters
    // ==========================================
    
    /**
     * Constructor por defecto requerido por JPA.
     */
    public Incidencia() {}

    /**
     * Obtiene el identificador único de la incidencia.
     * @return El ID de la incidencia.
     */
    public Integer getIdIncidencia() {
        return idIncidencia; 
    }

    /**
     * Establece el identificador único de la incidencia.
     * @param idIncidencia El nuevo ID a asignar.
     */
    public void setIdIncidencia(Integer idIncidencia) {
        this.idIncidencia = idIncidencia; 
    }

    /**
     * Obtiene el estado actual del ticket.
     * @return El estado (ej. PENDIENTE, EN PROCESO, RESUELTA).
     */
    public String getEstado() { 
        return estado; 
    }

    /**
     * Actualiza el estado de resolución del ticket.
     * @param estado El nuevo estado a asignar.
     */
    public void setEstado(String estado) {
        this.estado = estado; 
    }

    /**
     * Obtiene el nivel de urgencia de la incidencia.
     * @return La prioridad (ej. ALTA, MEDIA, BAJA).
     */
    public String getPrioridad() {
        return prioridad; 
    }

    /**
     * Establece el nivel de urgencia de la incidencia.
     * @param prioridad La nueva prioridad a asignar.
     */
    public void setPrioridad(String prioridad) { 
        this.prioridad = prioridad; 
    }

    /**
     * Obtiene la fecha y hora exacta en que el docente reportó el problema.
     * @return La fecha de creación.
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion; 
    }

    /**
     * Establece la fecha de creación del registro.
     * @param fechaCreacion La fecha a registrar.
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion; 
    }

    /**
     * Obtiene la fecha y hora en que el área de Soporte TI solucionó el problema.
     * @return La fecha de cierre, o null si aún no está resuelta.
     */
    public LocalDateTime getFechaCierre() {
        return fechaCierre; 
    }

    /**
     * Establece la fecha y hora de resolución del ticket.
     * @param fechaCierre La fecha de finalización.
     */
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre; 
    }
    
    /**
     * Obtiene el comentario de auditoría o revisión por parte del administrador.
     * @return El texto del comentario administrativo.
     */
    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    /**
     * Agrega o actualiza un comentario administrativo al ticket.
     * @param comentarioAdmin El texto del comentario.
     */
    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }

    /**
     * Obtiene la clasificación técnica del problema reportado.
     * @return La categoría (ej. Hardware, Redes, Software).
     */
    public String getCategoria() {
        return categoria; 
    }

    /**
     * Establece la clasificación técnica de la incidencia.
     * @param categoria La nueva categoría.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria; 
    }

    /**
     * Obtiene el lugar físico donde se requiere la asistencia.
     * @return La ubicación (ej. Laboratorio G302).
     */
    public String getUbicacion() {
        return ubicacion; 
    }

    /**
     * Establece el lugar físico de la incidencia.
     * @param ubicacion La nueva ubicación.
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion; 
    }

    /**
     * Obtiene el nombre del personal técnico asignado para resolver el problema.
     * @return El nombre del técnico responsable.
     */
    public String getAsignadoA() {
        return asignadoA; 
    }

    /**
     * Asigna un técnico responsable a la incidencia.
     * @param asignadoA El nombre del técnico.
     */
    public void setAsignadoA(String asignadoA) {
        this.asignadoA = asignadoA; 
    }

    /**
     * Obtiene la descripción corta o título principal del problema.
     * @return El título descriptivo de la incidencia.
     */
    public String getTipoIncidencia() {
        return tipoIncidencia; 
    }

    /**
     * Establece el título descriptivo del problema reportado.
     * @param tipoIncidencia El título de la incidencia.
     */
    public void setTipoIncidencia(String tipoIncidencia) {
        this.tipoIncidencia = tipoIncidencia; 
    }

    /**
     * Obtiene el nombre del archivo de la fotografía que evidencia la solución del ticket.
     * @return El nombre del archivo en el servidor.
     */
    public String getEvidenciaUrl() {
        return evidenciaUrl;
    }

    /**
     * Registra el nombre del archivo de la evidencia fotográfica subida por el técnico.
     * @param evidenciaUrl El nombre del archivo (ej. evidencia_ticket_1.jpg).
     */
    public void setEvidenciaUrl(String evidenciaUrl) {
        this.evidenciaUrl = evidenciaUrl;
    }

    /**
     * Obtiene los datos del usuario (docente) que creó la solicitud original.
     * @return La entidad Usuario asociada.
     */
    public Usuario getUsuario() {
        return usuario; 
    }

    /**
     * Asocia la incidencia al usuario que la reporta.
     * @param usuario El objeto de usuario correspondiente.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario; 
    }

    /**
     * Obtiene el detalle de la solución o rechazo de la incidencia.
     * @return El detalle de la resolución.
     */
    public String getResolucion() {
        return resolucion;
    }

    /**
     * Establece el detalle de la solución o rechazo de la incidencia.
     * @param resolucion El nuevo detalle de resolución a asignar.
     */
    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }
}