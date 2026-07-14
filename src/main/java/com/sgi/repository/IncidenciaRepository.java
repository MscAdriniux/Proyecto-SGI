package com.sgi.repository;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Verifica si existe una incidencia activa de un tipo específico en una ubicación dada.
     * Utilizado para prevenir la creación de tickets duplicados en el sistema.
     * @param ubicacion El lugar físico de la incidencia.
     * @param tipoIncidencia La descripción o título del problema.
     * @param estados Lista de estados considerados "activos" (ej. PENDIENTE, EN PROCESO).
     * @return true si existe al menos una coincidencia, false en caso contrario.
     */
    boolean existsByUbicacionAndTipoIncidenciaAndEstadoIn(String ubicacion, String tipoIncidencia, List<String> estados);

    /**
     * Busca todas las incidencias que han sido asignadas a un técnico en particular.
     * @param asignadoA El nombre del técnico responsable.
     * @return Una lista con las incidencias asignadas a dicho técnico.
     */
    List<Incidencia> findByAsignadoA(String asignadoA);

    
    
    // ==========================================
    // CONSULTAS PARA EL CENTRO DE MÉTRICAS 
    // ==========================================

    // 1. KPIs Diarios
    @Query(value = "SELECT COUNT(*) FROM incidencia WHERE DATE(fecha_creacion) = CURDATE()"
            , nativeQuery = true)
    long countCreadasHoy();

    @Query(value = "SELECT COUNT(*) FROM incidencia WHERE DATE(fecha_cierre) ="
            + " CURDATE() AND estado IN ('RESUELTA', 'ATENDIDA')", nativeQuery = true)
    long countResueltasHoy();

    // 2. Rendimiento de Técnicos (Toggle Hoy / Semana)
    @Query(value = "SELECT asignado_a, COUNT(*) FROM incidencia WHERE DATE(fecha_cierre)"
            + " = CURDATE() AND estado IN ('RESUELTA', 'ATENDIDA')"
            + " AND asignado_a IS NOT NULL GROUP BY asignado_a", nativeQuery = true)
    List<Object[]> countResueltasPorTecnicoHoy();

    @Query(value = "SELECT asignado_a, COUNT(*) FROM incidencia WHERE YEARWEEK(fecha_cierre, 1)"
            + " = YEARWEEK(CURDATE(), 1) AND estado IN ('RESUELTA', 'ATENDIDA')"
            + " AND asignado_a IS NOT NULL GROUP BY asignado_a", nativeQuery = true)
    List<Object[]> countResueltasPorTecnicoSemana();

    // 3. Estratégico y Puntos Críticos
    @Query(value = "SELECT ubicacion, COUNT(*) as total FROM"
            + " incidencia GROUP BY ubicacion ORDER BY total DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5UbicacionesProblematicas();

    @Query(value = "SELECT categoria, COUNT(*) FROM incidencia GROUP BY categoria", nativeQuery = true)
    List<Object[]> countIncidenciasPorCategoria();

    // 4.  Aulas Bloqueadas (Problemas Escalados)
    @Query(value = "SELECT ubicacion, tipo_incidencia, fecha_creacion FROM"
            + " incidencia WHERE estado = 'ATENDIDA'", nativeQuery = true)
    List<Object[]> findAulasBloqueadas();
}
    
