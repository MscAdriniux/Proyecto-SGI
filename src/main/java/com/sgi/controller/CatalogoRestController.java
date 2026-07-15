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
    public ResponseEntity<CatalogoIncidencia> crearItem(@RequestBody CatalogoIncidencia item) {
        CatalogoIncidencia nuevo = catalogoService.guardar(item);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoIncidencia> actualizarItem(@PathVariable Long id, @RequestBody CatalogoIncidencia item) {
        CatalogoIncidencia existente = catalogoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        existente.setNombre(item.getNombre());
        existente.setCategoria(item.getCategoria());
        existente.setPrioridad(item.getPrioridad());
        CatalogoIncidencia actualizado = catalogoService.guardar(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        CatalogoIncidencia existente = catalogoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        catalogoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
