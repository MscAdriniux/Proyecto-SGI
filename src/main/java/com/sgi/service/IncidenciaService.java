package com.sgi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.IncidenciaRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {

    // Registra operaciones relacionadas con las incidencias
    private static final Logger logger =
            LoggerFactory.getLogger(IncidenciaService.class);

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    // Obtener las incidencias de un docente en específico
    public List<Incidencia> obtenerPorUsuario(Usuario usuario) {
        return incidenciaRepository.findByUsuario(usuario);
    }

    // Guardar una incidencia
    public void guardar(Incidencia incidencia) {

        // Registrar la creación de una nueva incidencia
        logger.info(
                "Nueva incidencia registrada. Tipo: {}, Prioridad: {}",
                incidencia.getTipoIncidencia(),
                incidencia.getPrioridad()
        );

        incidenciaRepository.save(incidencia);
    }

    // Obtener todas las incidencias
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    // Buscar incidencia por ID
    public Incidencia obtenerPorId(Integer id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    public Incidencia cambiarEstado(Long id, String estado) {

        Incidencia incidencia = incidenciaRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));

        // Registrar actualización del estado de la incidencia
        logger.info(
                "Cambio de estado en incidencia {} -> {}",
                id,
                estado
        );

        incidencia.setEstado(estado);

        return incidenciaRepository.save(incidencia);
    }

    public Incidencia agregarComentario(Long id, String comentario) {

        Incidencia incidencia = incidenciaRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));

        // Registrar agregado de comentario técnico
        logger.info(
                "Comentario agregado a incidencia {}",
                id
        );

        incidencia.setComentarioAdmin(comentario);

        return incidenciaRepository.save(incidencia);
    }

    public List<Incidencia> obtenerPorEstado(String estado) {
        return incidenciaRepository.findByEstado(estado);
    }
}
