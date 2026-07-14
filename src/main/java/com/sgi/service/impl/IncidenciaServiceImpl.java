package com.sgi.service.impl;

import com.sgi.service.IncidenciaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.IncidenciaRepository;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de manejar la lógica de negocio de las incidencias.
 * Actúa como un intermediario seguro entre los controladores (vistas) y 
 * el repositorio (base de datos).
 */
@Service
public class IncidenciaServiceImpl implements IncidenciaService {
    
    /**
     * Constructor por defecto.
     */
    public IncidenciaServiceImpl() {}

    // Registra operaciones relacionadas con las incidencias
    private static final Logger logger =
            LoggerFactory.getLogger(IncidenciaServiceImpl.class);

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    // Obtener las incidencias de un docente en específico
    @Override
    public List<Incidencia> obtenerPorUsuario(Usuario usuario) {
        return incidenciaRepository.findByUsuario(usuario);
    }

    // Guardar una incidencia
    @Override
    public void guardar(Incidencia incidencia) {
        // Asignación de prioridad automática en el backend basada en la categoría
        if (incidencia.getCategoria() != null) {
            String cat = incidencia.getCategoria().toLowerCase();
            if (cat.contains("hardware") || cat.contains("red")) {
                incidencia.setPrioridad("ALTA");
            } else if (cat.contains("software") || cat.contains("equipamiento")) {
                incidencia.setPrioridad("MEDIA");
            } else {
                incidencia.setPrioridad("BAJA");
            }
        } else if (incidencia.getPrioridad() == null) {
            incidencia.setPrioridad("BAJA");
        }
        
        incidenciaRepository.save(incidencia);
    }

    // =============================
    // MÉTODOS PARA EL PANEL DE TI Y ADMIN
    // =============================

    /**
     * Recupera absolutamente todos los tickets de la base de datos, sin filtros.
     * Utilizado principalmente para el dashboard de Administradores.
     * @return Lista completa de todas las incidencias del sistema.
     */
    @Override
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    // Buscar incidencia por ID
    @Override
    public Incidencia obtenerPorId(Integer id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    @Override
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

    @Override
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

    
    /**
     * Filtra y obtiene todas las incidencias que se encuentren en un estado particular.
     * @param estado El estado a buscar (ej. "PENDIENTE", "RESUELTA").
     * @return Lista de incidencias que coincidan con el estado solicitado.
     */
    @Override
    public List<Incidencia> obtenerPorEstado(String estado) {
        return incidenciaRepository.findByEstado(estado);
    }

    /**
     * Obtiene las incidencias correspondientes al panel de un técnico.
     * Incluye las incidencias "PENDIENTES" (disponibles para tomar) y 
     * las que ya le han sido asignadas.
     * @param nombreTecnico Nombre completo del técnico.
     * @return Lista combinada de incidencias para el técnico.
     */
    @Override
    public List<Incidencia> obtenerIncidenciasParaTecnico(String nombreTecnico) {
        // Envolvemos en ArrayList para poder mutar la lista con .addAll()
        List<Incidencia> misIncidencias = new ArrayList<>(incidenciaRepository.findByAsignadoA(nombreTecnico));
        
        List<Incidencia> pendientes = incidenciaRepository.findByEstado("PENDIENTE");
        
        misIncidencias.addAll(pendientes);
        
        return misIncidencias;
    }

    /**
     * Verifica si existe una incidencia activa del mismo tipo en una ubicación específica.
     * Evita la duplicidad de reportes para el mismo problema en la misma aula/laboratorio.
     * @param ubicacion El lugar físico (ej. Laboratorio G302).
     * @param tipoIncidencia El título o descripción del problema.
     * @return true si ya existe un reporte en curso, false si es seguro registrarlo.
     */
    /**
     * Verifica si existe una incidencia activa del mismo tipo en una ubicación específica.
     * Ahora incluye el estado ATENDIDA como un bloqueo activo.
     */
    @Override
    public boolean existeActivaEnUbicacion(String ubicacion, String tipoIncidencia) {
        return incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
            ubicacion, 
            tipoIncidencia, 
            List.of("PENDIENTE", "EN PROCESO", "ATENDIDA")
        );
    }

    /**
     * Método auxiliar para verificar si el bloqueo se debe específicamente a un ticket ATENDIDO.
     */
    @Override
    public boolean esIncidenciaAtendida(String ubicacion, String tipoIncidencia) {
        return incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
            ubicacion, 
            tipoIncidencia, 
            List.of("ATENDIDA")
        );
    }
    
  
    /**
     * Obtiene una lista filtrada de incidencias a partir de sus IDs.
     * Utilizado por el Centro de Reportes para exportación selectiva.
     */
    @Override
    public List<Incidencia> obtenerPorListaIds(List<Integer> ids) {
        return incidenciaRepository.findAllById(ids);
    }
}
