package com.sgi.repository;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    
    // Método de tu avance (main): Para el panel del docente
    List<Incidencia> findByUsuario(Usuario usuario);
    
    // Método de Kriss: Para buscar por estados (ej. reportes)
    List<Incidencia> findByEstado(String estado);
    
}