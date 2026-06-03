package com.sgi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaGestionIncidencias {

    public static void main(String[] args) {
        SpringApplication.run(SistemaGestionIncidencias.class, args);
        System.out.println("El backend del SGI esta corriendo correctamente!");
    }
} 