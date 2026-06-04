package com.sgi.repository;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Spring Data JPA para la entidad Incidencia.
 * Proporciona los métodos estándar de acceso a datos (CRUD) y permite definir 
 * consultas personalizadas a la base de datos.
 */
@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    
    /**
     * Busca todas las incidencias creadas por un docente en particular.
     * Spring Boot genera automáticamente el "SELECT * FROM incidencia WHERE id_usuario = ?"
     * @param usuario El objeto Usuario del cual se quieren obtener los reportes.
     * @return Una lista con las incidencias pertenecientes a ese usuario.
     */
    List<Incidencia> findByUsuario(Usuario usuario);
    
    /**
     * Busca todas las incidencias que coincidan con un estado específico.
     * Útil para generar reportes administrativos o vistas filtradas.
     * @param estado El estado a buscar (ej. "PENDIENTE", "RESUELTA").
     * @return Una lista con las incidencias que tengan ese estado.
     */
    List<Incidencia> findByEstado(String estado);
    
}