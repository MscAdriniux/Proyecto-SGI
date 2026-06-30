package com.sgi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aula")
    private Long idAula;

    @ManyToOne
    @JoinColumn(name = "id_pabellon", nullable = false)
    private Pabellon pabellon;

    @Column(length = 5)
    private String nivel;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 100)
    private String tipo;

    public Aula() {
    }

public String getCodigoVisual() {
        if (this.pabellon != null && this.numero != null) {
            // Si el nivel no es nulo y es "S", lo usamos. Si es nulo, lo dejamos vacío.
            String prefijoNivel = (this.nivel != null && this.nivel.equalsIgnoreCase("S")) ? "S" : "";
            return this.pabellon.getNombre() + prefijoNivel + this.numero;
        }
        return "";
    }
    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public Pabellon getPabellon() {
        return pabellon;
    }

    public void setPabellon(Pabellon pabellon) {
        this.pabellon = pabellon;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}