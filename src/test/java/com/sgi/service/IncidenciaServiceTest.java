package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.repository.IncidenciaRepository;
import com.sgi.service.impl.IncidenciaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de Incidencias.
 * Se aísla IncidenciaServiceImpl de la base de datos usando Mockito para
 * simular (mock) el comportamiento de IncidenciaRepository, validando
 * únicamente la lógica de negocio propia del servicio.
 */
@ExtendWith(MockitoExtension.class)
public class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @InjectMocks
    private IncidenciaServiceImpl incidenciaService;

    // ==========================================
    // 1. ASIGNACIÓN AUTOMÁTICA DE PRIORIDAD (método guardar)
    // ==========================================

    @Test
    public void testGuardar_AsignaPrioridadAlta_CuandoCategoriaEsHardware() {
        // Arrange
        Incidencia incidencia = new Incidencia();
        incidencia.setCategoria("Hardware");

        // Act
        incidenciaService.guardar(incidencia);

        // Assert
        assertEquals("ALTA", incidencia.getPrioridad());
        verify(incidenciaRepository, times(1)).save(incidencia);
    }

    @Test
    public void testGuardar_AsignaPrioridadMedia_CuandoCategoriaEsSoftware() {
        // Arrange
        Incidencia incidencia = new Incidencia();
        incidencia.setCategoria("Software");

        // Act
        incidenciaService.guardar(incidencia);

        // Assert
        assertEquals("MEDIA", incidencia.getPrioridad());
    }

    @Test
    public void testGuardar_AsignaPrioridadBaja_CuandoCategoriaNoCoincideConReglas() {
        // Arrange: una categoría que no contiene "hardware", "red", "software" ni "equipamiento"
        Incidencia incidencia = new Incidencia();
        incidencia.setCategoria("Mobiliario");

        // Act
        incidenciaService.guardar(incidencia);

        // Assert
        assertEquals("BAJA", incidencia.getPrioridad());
    }

    @Test
    public void testGuardar_AsignaPrioridadBaja_CuandoNoHayCategoriaNiPrioridad() {
        // Arrange: caso borde, ni categoría ni prioridad fueron enviadas desde el formulario
        Incidencia incidencia = new Incidencia();

        // Act
        incidenciaService.guardar(incidencia);

        // Assert: el servicio no debe dejar la prioridad en null, es un salvavidas de negocio
        assertEquals("BAJA", incidencia.getPrioridad());
    }

    // ==========================================
    // 2. VALIDACIÓN DE DUPLICADOS EN UBICACIÓN
    // ==========================================

    @Test
    public void testExisteActivaEnUbicacion_RetornaTrue_CuandoYaHayUnTicketActivo() {
        // Arrange
        when(incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
                "Laboratorio G302", "Proyector no enciende",
                List.of("PENDIENTE", "EN PROCESO", "ATENDIDA")))
            .thenReturn(true);

        // Act
        boolean resultado = incidenciaService.existeActivaEnUbicacion("Laboratorio G302", "Proyector no enciende");

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void testExisteActivaEnUbicacion_RetornaFalse_CuandoNoHayTicketsActivos() {
        // Arrange
        when(incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
                anyString(), anyString(), anyList()))
            .thenReturn(false);

        // Act
        boolean resultado = incidenciaService.existeActivaEnUbicacion("Laboratorio G302", "Proyector no enciende");

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void testEsIncidenciaAtendida_ConsultaSoloElEstadoAtendida() {
        // Arrange
        when(incidenciaRepository.existsByUbicacionAndTipoIncidenciaAndEstadoIn(
                "Laboratorio G302", "Proyector no enciende", List.of("ATENDIDA")))
            .thenReturn(true);

        // Act
        boolean resultado = incidenciaService.esIncidenciaAtendida("Laboratorio G302", "Proyector no enciende");

        // Assert
        assertTrue(resultado);
        // Verificamos que la consulta se hizo únicamente con el estado ATENDIDA, no con la lista completa
        verify(incidenciaRepository).existsByUbicacionAndTipoIncidenciaAndEstadoIn(
                "Laboratorio G302", "Proyector no enciende", List.of("ATENDIDA"));
    }

    // ==========================================
    // 3. ORDENAMIENTO POR PRIORIDAD (lógica movida desde los controladores)
    // ==========================================

    @Test
    public void testObtenerOrdenadasPorPrioridad_OrdenaAltaAntesQueMediaYBaja() {
        // Arrange
        Incidencia baja = crearIncidencia("BAJA", LocalDateTime.now());
        Incidencia alta = crearIncidencia("ALTA", LocalDateTime.now());
        Incidencia media = crearIncidencia("MEDIA", LocalDateTime.now());

        // Act
        List<Incidencia> ordenadas = incidenciaService.obtenerOrdenadasPorPrioridad(
                List.of(baja, alta, media));

        // Assert
        assertEquals("ALTA", ordenadas.get(0).getPrioridad());
        assertEquals("MEDIA", ordenadas.get(1).getPrioridad());
        assertEquals("BAJA", ordenadas.get(2).getPrioridad());
    }

    @Test
    public void testObtenerOrdenadasPorPrioridad_DesempataPorFechaMasReciente() {
        // Arrange: misma prioridad, distinta fecha de creación
        Incidencia antigua = crearIncidencia("ALTA", LocalDateTime.now().minusDays(2));
        Incidencia reciente = crearIncidencia("ALTA", LocalDateTime.now());

        // Act
        List<Incidencia> ordenadas = incidenciaService.obtenerOrdenadasPorPrioridad(
                List.of(antigua, reciente));

        // Assert: la más reciente debe aparecer primero
        assertEquals(reciente, ordenadas.get(0));
        assertEquals(antigua, ordenadas.get(1));
    }

    // ==========================================
    // 4. BÚSQUEDA POR ID (caso borde)
    // ==========================================

    @Test
    public void testObtenerPorId_RetornaNull_CuandoElTicketNoExiste() {
        // Arrange
        when(incidenciaRepository.findById(999)).thenReturn(java.util.Optional.empty());

        // Act
        Incidencia resultado = incidenciaService.obtenerPorId(999);

        // Assert: el servicio no debe lanzar excepción, debe devolver null de forma controlada
        assertNull(resultado);
    }

    // ==========================================
    // Método auxiliar para construir incidencias de prueba
    // ==========================================
    private Incidencia crearIncidencia(String prioridad, LocalDateTime fechaCreacion) {
        Incidencia incidencia = new Incidencia();
        incidencia.setPrioridad(prioridad);
        incidencia.setFechaCreacion(fechaCreacion);
        return incidencia;
    }
}