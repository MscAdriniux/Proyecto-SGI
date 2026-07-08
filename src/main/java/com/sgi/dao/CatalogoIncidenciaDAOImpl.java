package com.sgi.dao;

import com.sgi.model.CatalogoIncidencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class CatalogoIncidenciaDAOImpl implements CatalogoIncidenciaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CatalogoIncidencia> obtenerTodo() {
        return entityManager.createQuery("FROM CatalogoIncidencia", CatalogoIncidencia.class).getResultList();
    }

    @Override
    public CatalogoIncidencia obtenerPorId(Long id) {
        return entityManager.find(CatalogoIncidencia.class, id);
    }

    @Override
    public CatalogoIncidencia guardar(CatalogoIncidencia catalogoIncidencia) {
        if (catalogoIncidencia.getId() == null) {
            entityManager.persist(catalogoIncidencia);
            return catalogoIncidencia;
        } else {
            return entityManager.merge(catalogoIncidencia);
        }
    }

    @Override
    public void eliminar(Long id) {
        CatalogoIncidencia catalogoIncidencia = obtenerPorId(id);
        if (catalogoIncidencia != null) {
            entityManager.remove(catalogoIncidencia);
        }
    }
}
