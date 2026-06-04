package com.sgi.repository;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    
    // Spring Boot crea la consulta SQL automáticamente gracias a este nombre
    List<Incidencia> findByUsuario(Usuario usuario);
    
    // Comprobar si existe una incidencia activa en la misma ubicación
    boolean existsByUbicacionAndTipoIncidenciaAndEstadoIn(String ubicacion, String tipoIncidencia, List<String> estados);

    // Buscar incidencias por técnico asignado
    List<Incidencia> findByAsignadoA(String asignadoA);
}