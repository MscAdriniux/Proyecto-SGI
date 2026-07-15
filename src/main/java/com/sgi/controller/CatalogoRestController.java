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

    /**
     * Crea un nuevo ítem en el catálogo de incidencias.
     * Usado por el modal "Agregar Incidencia al Catálogo" del panel de administrador.
     */
    @PostMapping
    public ResponseEntity<CatalogoIncidencia> crear(@RequestBody CatalogoIncidencia item) {
        item.setId(null); // Nos aseguramos de que sea un registro nuevo, ignorando cualquier id enviado
        CatalogoIncidencia creado = catalogoService.guardar(item);
        return ResponseEntity.ok(creado);
    }

    /**
     * Actualiza un ítem existente del catálogo de incidencias.
     * Usado por el modal "Editar Incidencia en el Catálogo" del panel de administrador.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CatalogoIncidencia> actualizar(@PathVariable Long id, @RequestBody CatalogoIncidencia item) {
        item.setId(id); // El id viene de la URL, no del cuerpo, para evitar inconsistencias
        CatalogoIncidencia actualizado = catalogoService.guardar(item);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un ítem del catálogo de incidencias.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}