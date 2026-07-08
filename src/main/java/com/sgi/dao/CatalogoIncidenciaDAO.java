package com.sgi.dao;

import com.sgi.model.CatalogoIncidencia;
import java.util.List;

public interface CatalogoIncidenciaDAO {
    List<CatalogoIncidencia> obtenerTodo();
    CatalogoIncidencia obtenerPorId(Long id);
    CatalogoIncidencia guardar(CatalogoIncidencia catalogoIncidencia);
    void eliminar(Long id);
}
