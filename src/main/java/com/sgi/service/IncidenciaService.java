package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.IncidenciaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    // Obtener las incidencias de un docente en específico
    public List<Incidencia> obtenerPorUsuario(Usuario usuario) {
        return incidenciaRepository.findByUsuario(usuario);
    }
    
    // Método para guardar una nueva incidencia en la base de datos
    public void guardar(Incidencia incidencia) {
        incidenciaRepository.save(incidencia);
    }
}