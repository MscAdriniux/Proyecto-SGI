/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import java.util.List;

/**
 *
 * @author ad_ri
 */
public interface IncidenciaService {

    Incidencia agregarComentario(Long id, String comentario);

    Incidencia cambiarEstado(Long id, String estado);

    /**
     * Método auxiliar para verificar si el bloqueo se debe específicamente a un ticket ATENDIDO.
     */
    boolean esIncidenciaAtendida(String ubicacion, String tipoIncidencia);

    /**
     * Verifica si existe una incidencia activa del mismo tipo en una ubicación específica.
     * Evita la duplicidad de reportes para el mismo problema en la misma aula/laboratorio.
     * @param ubicacion El lugar físico (ej. Laboratorio G302).
     * @param tipoIncidencia El título o descripción del problema.
     * @return true si ya existe un reporte en curso, false si es seguro registrarlo.
     */
    /**
     * Verifica si existe una incidencia activa del mismo tipo en una ubicación específica.
     * Ahora incluye el estado ATENDIDA como un bloqueo activo.
     */
    boolean existeActivaEnUbicacion(String ubicacion, String tipoIncidencia);

    // Guardar una incidencia
    void guardar(Incidencia incidencia);

    /**
     * Obtiene las incidencias correspondientes al panel de un técnico.
     * Incluye las incidencias "PENDIENTES" (disponibles para tomar) y
     * las que ya le han sido asignadas.
     * @param nombreTecnico Nombre completo del técnico.
     * @return Lista combinada de incidencias para el técnico.
     */
    List<Incidencia> obtenerIncidenciasParaTecnico(String nombreTecnico);

    /**
     * Filtra y obtiene todas las incidencias que se encuentren en un estado particular.
     * @param estado El estado a buscar (ej. "PENDIENTE", "RESUELTA").
     * @return Lista de incidencias que coincidan con el estado solicitado.
     */
    List<Incidencia> obtenerPorEstado(String estado);

    // Buscar incidencia por ID
    Incidencia obtenerPorId(Integer id);

    /**
     * Obtiene una lista filtrada de incidencias a partir de sus IDs.
     * Utilizado por el Centro de Reportes para exportación selectiva.
     */
    List<Incidencia> obtenerPorListaIds(List<Integer> ids);

    // Obtener las incidencias de un docente en específico
    List<Incidencia> obtenerPorUsuario(Usuario usuario);

    // =============================
    // MÉTODOS PARA EL PANEL DE TI Y ADMIN
    // =============================
    /**
     * Recupera absolutamente todos los tickets de la base de datos, sin filtros.
     * Utilizado principalmente para el dashboard de Administradores.
     * @return Lista completa de todas las incidencias del sistema.
     */
    List<Incidencia> obtenerTodas();

    /**
     * Ordena una lista de incidencias aplicando la regla de negocio de prioridad:
     * primero ALTA, luego MEDIA, luego BAJA/sin definir; y dentro de un mismo
     * nivel de prioridad, la incidencia más reciente aparece primero.
     * @param incidencias La lista de incidencias a ordenar (no se modifica la original).
     * @return Una nueva lista ordenada según prioridad y fecha de creación.
     */
    List<Incidencia> obtenerOrdenadasPorPrioridad(List<Incidencia> incidencias);

}