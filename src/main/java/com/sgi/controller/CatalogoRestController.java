package com.sgi.controller;

import com.sgi.model.CatalogoIncidencia;
import com.sgi.service.CatalogoService; // 1. Importamos el servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoRestController {

    @Autowired
    private CatalogoService catalogoService; 

    @GetMapping
    public ResponseEntity<List<CatalogoIncidencia>> obtenerCatalogo() {
        List<CatalogoIncidencia> catalogo = catalogoService.obtenerTodoElCatalogo(); // 3. Llamamos al servicio
        
        if (catalogo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(catalogo);
    }
}