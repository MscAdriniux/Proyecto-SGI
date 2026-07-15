package com.sgi.repository;

import com.sgi.model.CatalogoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoIncidenciaRepository extends JpaRepository<CatalogoIncidencia, Long> {
  
}