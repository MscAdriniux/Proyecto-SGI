package com.sgi.controller;

import com.sgi.model.Incidencia;
import com.sgi.service.IncidenciaService;
import com.sgi.service.IncidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@CrossOrigin(origins = "*")
public class IncidenciaController {

    @Autowired
    private IncidenciaService incidenciaService;

    @GetMapping
    public List<Incidencia> listar() {
        return incidenciaService.obtenerTodas();
    }

    @PostMapping
    public Incidencia crear(@RequestBody Incidencia incidencia) {
        return incidenciaService.guardarIncidencia(incidencia);
    }
}