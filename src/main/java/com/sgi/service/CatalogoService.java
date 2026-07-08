package com.sgi.service;

import com.sgi.dao.CatalogoIncidenciaDAO;
import com.sgi.model.CatalogoIncidencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service 
public class CatalogoService {

    @Autowired
    private CatalogoIncidenciaDAO catalogoDAO;

    public List<CatalogoIncidencia> obtenerTodoElCatalogo() {
        return catalogoDAO.obtenerTodo();
    }

    public CatalogoIncidencia guardar(CatalogoIncidencia catalogoIncidencia) {
        return catalogoDAO.guardar(catalogoIncidencia);
    }

    public CatalogoIncidencia obtenerPorId(Long id) {
        return catalogoDAO.obtenerPorId(id);
    }

    public void eliminar(Long id) {
        catalogoDAO.eliminar(id);
    }
}