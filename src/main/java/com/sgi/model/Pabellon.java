package com.sgi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pabellon")
public class Pabellon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pabellon")
    private Long idPabellon;

    @Column(nullable = false, length = 5)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String sede;

    public Pabellon() {
    }

    public Long getIdPabellon() {
        return idPabellon;
    }

    public void setIdPabellon(Long idPabellon) {
        this.idPabellon = idPabellon;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }
}