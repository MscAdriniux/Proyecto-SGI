package com.sgi.service.impl;

import com.sgi.service.CatalogoService;
import com.sgi.model.CatalogoIncidencia;
import com.sgi.repository.CatalogoIncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service 
public class CatalogoServiceImpl implements CatalogoService {

    @Autowired
    private CatalogoIncidenciaRepository repository;

    @Override
    public List<CatalogoIncidencia> obtenerTodoElCatalogo() {
        return repository.findAll();
    }

    @Override
    public CatalogoIncidencia guardar(CatalogoIncidencia item) {
        // save() de Spring Data JPA hace INSERT si el id es null, o UPDATE si ya existe
        return repository.save(item);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}