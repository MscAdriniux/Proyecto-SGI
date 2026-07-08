package com.sgi.service;

import com.sgi.model.Incidencia;
import java.io.IOException;
import java.util.List;

public interface ReporteService {
    
    // Método estandarizado para generar el archivo
    byte[] generarReporte(List<Incidencia> incidencias) throws IOException;
    
    // Método que le dirá al controlador si esta clase maneja el formato solicitado
    boolean soportaFormato(String formato);
}