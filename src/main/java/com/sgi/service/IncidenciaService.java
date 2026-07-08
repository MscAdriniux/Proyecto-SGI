package com.sgi.service;

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
public class IncidenciaService {
    
    /**
     * Constructor por defecto.
     */
    public IncidenciaService() {}

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    /**
     * Obtiene una lista de todas las incidencias reportadas por un docente específico.
     * @param usuario El usuario del cual se desean consultar los tickets.
     * @return Lista de incidencias asociadas a ese usuario.
     */
    public List<Incidencia> obtenerPorUsuario(Usuario usuario) {
        return incidenciaRepository.findByUsuario(usuario);
    }
    
    /**
     * Guarda una nueva incidencia o actualiza una existente en la base de datos.
     * Aplica reglas de negocio para asignación automática de prioridad basada en la categoría.
     * @param incidencia El objeto incidencia con los datos a persistir.
     */
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
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    /**
     * Busca todas las incidencias que han sido asignadas a un técnico específico.
     * @param asignadoA El nombre o identificador del técnico de TI.
     * @return Lista de incidencias a cargo de dicho técnico.
     */
    public List<Incidencia> obtenerPorAsignadoA(String asignadoA) {
        return incidenciaRepository.findByAsignadoA(asignadoA);
    }

    /**
     * Busca una incidencia específica utilizando su clave primaria (ID).
     * @param id El identificador único de la incidencia.
     * @return El objeto Incidencia si existe, o null si no se encuentra.
     */
    public Incidencia obtenerPorId(Integer id) {
        return incidenciaRepository.findById(id).orElse(null);
    }
    
    /**
     * Filtra y obtiene todas las incidencias que se encuentren en un estado particular.
     * @param estado El estado a buscar (ej. "PENDIENTE", "RESUELTA").
     * @return Lista de incidencias que coincidan con el estado solicitado.
     */
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
    public boolean existeActivaEnUbicacion(String ubicacion, String tipoIncidencia) {
        return incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
            ubicacion, 
            tipoIncidencia, 
            List.of("PENDIENTE", "EN PROCESO")
        );
    }
    
  
    /**
     * Obtiene una lista filtrada de incidencias a partir de sus IDs.
     * Utilizado por el Centro de Reportes para exportación selectiva.
     */
    public List<Incidencia> obtenerPorListaIds(List<Integer> ids) {
        return incidenciaRepository.findAllById(ids);
    }
}