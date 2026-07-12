package com.sgi.service;

import java.util.List;
import java.util.Map;

public interface MetricaService {
    
    Map<String, Object> obtenerKpisDiarios();
    
    Map<String, Long> obtenerRendimientoTecnicos(String periodo);
    
    Map<String, Long> obtenerTopUbicaciones();
    
    Map<String, Long> obtenerDistribucionCategorias();
    
    List<Map<String, Object>> obtenerAulasBloqueadas();
}