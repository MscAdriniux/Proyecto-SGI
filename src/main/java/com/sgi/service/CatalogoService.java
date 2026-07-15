package com.sgi.service;

import com.sgi.model.CatalogoIncidencia;
import com.sgi.repository.CatalogoIncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service 
public class CatalogoService {

    @Autowired
    private CatalogoIncidenciaRepository repository;

    public List<CatalogoIncidencia> obtenerTodoElCatalogo() {
        return repository.findAll();
    }

    public CatalogoIncidencia obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public CatalogoIncidencia guardar(CatalogoIncidencia catalogoIncidencia) {
        return repository.save(catalogoIncidencia);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}