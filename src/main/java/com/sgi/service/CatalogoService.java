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
    CatalogoIncidencia obtenerPorId(Long id);
    CatalogoIncidencia guardar(CatalogoIncidencia item);
    void eliminar(Long id);
}