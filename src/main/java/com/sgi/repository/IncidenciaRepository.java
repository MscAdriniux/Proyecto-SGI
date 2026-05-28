package com.sgi.repository;

import com.sgi.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
  List<Incidencia> findByEstado(String estado);
}
