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

    public Incidencia cambiarEstado(Long id, String estado) {

        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));
    
        incidencia.setEstado(estado);
    
        return incidenciaRepository.save(incidencia);
    }
    
    public Incidencia agregarComentario(Long id, String comentario) {
    
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));
    
        incidencia.setComentarioAdmin(comentario);
    
        return incidenciaRepository.save(incidencia);
    }

    public List<Incidencia> obtenerPorEstado(String estado) {
        return incidenciaRepository.findByEstado(estado);
    }
}
