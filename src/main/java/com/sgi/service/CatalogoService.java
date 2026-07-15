/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sgi.service;

import com.sgi.model.CatalogoIncidencia;
import java.util.List;

/**
 *
 * @author ad_ri
 */
public interface CatalogoService {

    List<CatalogoIncidencia> obtenerTodoElCatalogo();

    /**
     * Crea o actualiza un ítem del catálogo de incidencias.
     * Si el ítem no trae ID, se crea uno nuevo; si ya tiene ID, se actualiza el existente.
     * @param item El ítem a guardar.
     * @return El ítem ya persistido (con su ID asignado si era nuevo).
     */
    CatalogoIncidencia guardar(CatalogoIncidencia item);

    /**
     * Elimina un ítem del catálogo de incidencias por su ID.
     * @param id El identificador del ítem a eliminar.
     */
    void eliminar(Long id);

}