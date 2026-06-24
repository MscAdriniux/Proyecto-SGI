package com.sgi.service;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.IncidenciaRepository;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @InjectMocks
    private IncidenciaService incidenciaService;

    @Test
    public void registrarIncidenciaCorrectamente() {

        // Arrange
        Incidencia incidencia = new Incidencia();
        incidencia.setCategoria("Hardware");

        // Act
        incidenciaService.guardar(incidencia);

        // Assert
        assertEquals("ALTA", incidencia.getPrioridad());

        verify(incidenciaRepository)
                .save(incidencia);
    }

    @Test
    public void obtenerIncidenciasPorUsuario() {

        // Arrange
        Usuario usuario = new Usuario();

        Incidencia incidencia1 = new Incidencia();
        Incidencia incidencia2 = new Incidencia();

        List<Incidencia> incidencias =
                List.of(incidencia1, incidencia2);

        when(incidenciaRepository.findByUsuario(usuario))
                .thenReturn(incidencias);

        // Act
        List<Incidencia> resultado =
                incidenciaService.obtenerPorUsuario(usuario);

        // Assert
        assertEquals(2, resultado.size());

        verify(incidenciaRepository)
                .findByUsuario(usuario);
    }
}