package com.sgi.service.impl;

import com.sgi.service.MetricaService;
import com.sgi.repository.IncidenciaRepository;
import java.sql.Timestamp; // <-- IMPORTACIÓN CLAVE: Para manejar la fecha nativa de MySQL
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricaServiceImpl implements MetricaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Override
    public Map<String, Object> obtenerKpisDiarios() {
        Map<String, Object> kpis = new HashMap<>();
        long creadasHoy = incidenciaRepository.countCreadasHoy();
        long resueltasHoy = incidenciaRepository.countResueltasHoy();
        
        double tasaResolucion = 0.0;
        if (creadasHoy > 0) {
            tasaResolucion = ((double) resueltasHoy / creadasHoy) * 100;
        }

        kpis.put("creadasHoy", creadasHoy);
        kpis.put("resueltasHoy", resueltasHoy);
        kpis.put("tasaResolucion", Math.round(tasaResolucion * 100.0) / 100.0);
        
        return kpis;
    }

    @Override
    public Map<String, Long> obtenerRendimientoTecnicos(String periodo) {
        List<Object[]> resultados = "semana".equalsIgnoreCase(periodo) 
                ? incidenciaRepository.countResueltasPorTecnicoSemana() 
                : incidenciaRepository.countResueltasPorTecnicoHoy();

        Map<String, Long> rendimiento = new LinkedHashMap<>();
        for (Object[] row : resultados) {
            rendimiento.put((String) row[0], ((Number) row[1]).longValue());
        }
        return rendimiento;
    }

    @Override
    public Map<String, Long> obtenerTopUbicaciones() {
        List<Object[]> resultados = incidenciaRepository.findTop5UbicacionesProblematicas();
        Map<String, Long> ubicaciones = new LinkedHashMap<>();
        for (Object[] row : resultados) {
            ubicaciones.put((String) row[0], ((Number) row[1]).longValue());
        }
        return ubicaciones;
    }

    @Override
    public Map<String, Long> obtenerDistribucionCategorias() {
        List<Object[]> resultados = incidenciaRepository.countIncidenciasPorCategoria();
        Map<String, Long> categorias = new LinkedHashMap<>();
        for (Object[] row : resultados) {
            categorias.put((String) row[0], ((Number) row[1]).longValue());
        }
        return categorias;
    }

    @Override
    public List<Map<String, Object>> obtenerAulasBloqueadas() {
        List<Object[]> resultados = incidenciaRepository.findAulasBloqueadas();
        List<Map<String, Object>> bloqueadas = new ArrayList<>();
        
        for (Object[] row : resultados) {
            Map<String, Object> aula = new HashMap<>();
            aula.put("ubicacion", row[0]);
            aula.put("problema", row[1]);
            
            // CORRECCIÓN: Convertimos el Timestamp de la consulta nativa a LocalDateTime
            Object fechaObj = row[2];
            if (fechaObj instanceof Timestamp) {
                aula.put("fechaEscalado", ((Timestamp) fechaObj).toLocalDateTime());
            } else {
                aula.put("fechaEscalado", fechaObj);
            }
            
            bloqueadas.add(aula);
        }
        return bloqueadas;
    }
}