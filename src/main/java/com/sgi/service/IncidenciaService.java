package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    public Incidencia guardarIncidencia(Incidencia incidencia) {
        return incidenciaRepository.save(incidencia);
    }
}