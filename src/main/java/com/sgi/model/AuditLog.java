package com.sgi.model;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class AuditLog {

    private LocalDateTime fecha;

    private String nivel;

    private String modulo;

    private String usuario;

    private String accion;

    public AuditLog() {
    }

    public AuditLog(LocalDateTime fecha,
                    String nivel,
                    String modulo,
                    String usuario,
                    String accion) {

        this.fecha = fecha;
        this.nivel = nivel;
        this.modulo = modulo;
        this.usuario = usuario;
        this.accion = accion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public String getFechaFormateada() {

        if (fecha == null) {
            return "";
        }

        return fecha.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        );

    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}