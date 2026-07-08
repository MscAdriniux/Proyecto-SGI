package com.sgi.controller;

import com.sgi.model.CatalogoIncidencia;
import com.sgi.service.CatalogoService; // 1. Importamos el servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<CatalogoIncidencia> crearIncidencia(@RequestBody CatalogoIncidencia catalogoIncidencia) {
        CatalogoIncidencia nuevo = catalogoService.guardar(catalogoIncidencia);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoIncidencia> actualizarIncidencia(@PathVariable Long id, @RequestBody CatalogoIncidencia catalogoIncidencia) {
        CatalogoIncidencia existente = catalogoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        existente.setNombre(catalogoIncidencia.getNombre());
        existente.setCategoria(catalogoIncidencia.getCategoria());
        existente.setPrioridad(catalogoIncidencia.getPrioridad());
        CatalogoIncidencia actualizado = catalogoService.guardar(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncidencia(@PathVariable Long id) {
        CatalogoIncidencia existente = catalogoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        catalogoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}