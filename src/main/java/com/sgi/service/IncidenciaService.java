package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.IncidenciaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    // Obtener las incidencias de un docente en específico
    public List<Incidencia> obtenerPorUsuario(Usuario usuario) {
        return incidenciaRepository.findByUsuario(usuario);
    }
    
    // Método para guardar una nueva incidencia en la base de datos
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
        } else {
            incidencia.setPrioridad("BAJA");
        }
        
        incidenciaRepository.save(incidencia);
    }

    // Obtener todas las incidencias (para el administrador)
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    // Obtener incidencias asignadas a un técnico
    public List<Incidencia> obtenerPorAsignadoA(String asignadoA) {
        // En nuestro diseño, asignadoA almacena el nombre/correo del técnico de TI
        // Buscamos todas las incidencias y las filtramos, o hacemos consulta en BD.
        // Pero primero busquemos si declaramos el método findByAsignadoA en el Repository.
        // Espera, ¿añadimos findByAsignadoA en IncidenciaRepository? No, se nos pasó en el paso anterior!
        // Vamos a agregarlo o filtrar en memoria. O mejor, modificar el repository para incluir findByAsignadoA.
        // Vamos a usar la consulta en BD que es más eficiente.
        return incidenciaRepository.findByAsignadoA(asignadoA);
    }

    // Obtener incidencia por su ID
    public Incidencia obtenerPorId(Integer id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    // Comprobar si existe una incidencia activa del mismo tipo en la misma ubicación
    public boolean existeActivaEnUbicacion(String ubicacion, String tipoIncidencia) {
        return incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
            ubicacion, 
            tipoIncidencia, 
            List.of("PENDIENTE", "EN PROCESO")
        );
    }
}