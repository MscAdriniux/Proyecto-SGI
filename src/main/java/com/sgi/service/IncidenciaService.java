package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.IncidenciaRepository;
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
     * @param incidencia El objeto incidencia con los datos a persistir.
     */
    public void guardar(Incidencia incidencia) {
        incidenciaRepository.save(incidencia);
    }

    // =============================
    // MÉTODOS PARA EL PANEL DE TI
    // =============================

    /**
     * Recupera absolutamente todos los tickets de la base de datos, sin filtros.
     * Utilizado principalmente para el dashboard del personal de Soporte TI.
     * @return Lista completa de todas las incidencias del sistema.
     */
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    /**
     * Busca una incidencia específica utilizando su clave primaria (ID).
     * @param id El identificador único de la incidencia.
     * @return El objeto Incidencia si existe, o null si no se encuentra (evita que el sistema colapse).
     */
    public Incidencia obtenerPorId(Integer id) {
        // El ".orElse(null)" significa: si no encuentra el ID, que devuelva nulo para que no se caiga el sistema
        return incidenciaRepository.findById(id).orElse(null);
    }
    
    /**
     * Filtra y obtiene todas las incidencias que se encuentren en un estado particular.
     * Ideal para generar reportes estadísticos o vistas categorizadas.
     * @param estado El estado a buscar (ej. "PENDIENTE", "RESUELTA").
     * @return Lista de incidencias que coincidan con el estado solicitado.
     */
    public List<Incidencia> obtenerPorEstado(String estado) {
        return incidenciaRepository.findByEstado(estado);
    }
}