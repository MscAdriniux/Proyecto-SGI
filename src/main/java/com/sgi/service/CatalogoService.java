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
}