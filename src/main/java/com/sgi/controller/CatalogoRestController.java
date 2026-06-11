package com.sgi.controller;

import com.sgi.model.CatalogoIncidencia;
import com.sgi.repository.CatalogoIncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoRestController {

    @Autowired
    private CatalogoIncidenciaRepository repository;

    @GetMapping
    public List<CatalogoIncidencia> obtenerCatalogo() {
        // Esto va a MySQL, saca todo el catálogo y lo transforma en JSON automáticamente
        return repository.findAll();
    }
}