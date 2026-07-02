package com.sgi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicializa el Sistema de Gestión de Incidencias (SGI).
 * <p>
 * Esta aplicación está construida sobre Spring Boot e implementa una arquitectura
 * MVC (Model-View-Controller) para gestionar los reportes de la comunidad universitaria.
 * </p>
 * <h2>Arquitectura y Modelado del Sistema</h2>
 * <p>
 * A continuación se presenta el diagrama estructural de las principales entidades
 * y su relación dentro del ecosistema del SGI:
 * </p>
 * <p>
 *  <img src="doc-files/diagrama_clases.png" 
         alt="Diagrama de Arquitectura del SGI" 
         style="width: 100%; max-width: 600px; height: auto; border: 1px solid #ccc; border-radius: 8px;">
 * </p>
 *
 * @author GrupoProyecto-SGI
 * @version 1.0
 */
@SpringBootApplication
public class SistemaGestionIncidencias {

    /**
     * Constructor por defecto.
     */
    public SistemaGestionIncidencias() {}

    /**
     * Método principal que inicia la ejecución del sistema.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(SistemaGestionIncidencias.class, args);
    }
}