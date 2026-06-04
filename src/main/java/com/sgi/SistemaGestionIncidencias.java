package com.sgi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot del Sistema de Gestión de Incidencias.
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