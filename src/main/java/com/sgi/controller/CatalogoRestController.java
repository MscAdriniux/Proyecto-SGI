package com.sgi.controller;

import com.sgi.model.CatalogoIncidencia;
import com.sgi.service.CatalogoService;
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
        List<CatalogoIncidencia> catalogo = catalogoService.obtenerTodoElCatalogo();

        if (catalogo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(catalogo);
    }

    @PostMapping
    public ResponseEntity<CatalogoIncidencia> crear(@RequestBody CatalogoIncidencia item) {
        CatalogoIncidencia nuevo = catalogoService.guardar(item);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoIncidencia> actualizar(@PathVariable Long id, @RequestBody CatalogoIncidencia item) {
        // Usamos la lógica de verificación de 'main' que es más segura
        CatalogoIncidencia existente = catalogoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Actualizamos los campos
        existente.setNombre(item.getNombre());
        existente.setCategoria(item.getCategoria());
        existente.setPrioridad(item.getPrioridad());
        
        CatalogoIncidencia actualizado = catalogoService.guardar(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Verificamos antes de borrar
        CatalogoIncidencia existente = catalogoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}