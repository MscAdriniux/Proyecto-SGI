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

    // Obtener las incidencias de un docente en específico (Panel Docente)
    public List<Incidencia> obtenerPorUsuario(Usuario usuario) {
        return incidenciaRepository.findByUsuario(usuario);
    }
    
    // Método para guardar una nueva incidencia en la base de datos
    public void guardar(Incidencia incidencia) {
        incidenciaRepository.save(incidencia);
    }

  
    // MÉTODOS PARA EL PANEL DE TI
    // =============================

    // Obtener absolutamente TODAS las incidencias de la base de datos
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    // Buscar una incidencia específica por su ID (Para actualizar su estado)
    public Incidencia obtenerPorId(Integer id) {
        // El ".orElse(null)" significa: si no encuentra el ID, que devuelva nulo para que no se caiga el sistema
        return incidenciaRepository.findById(id).orElse(null);
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
