package com.sgi.service.impl;

import com.sgi.service.AuditService;
import com.sgi.model.AuditLog;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

      private static final String LOG_FILE = "logs/sgi.log";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<AuditLog> obtenerLogs() {

        List<AuditLog> lista = new ArrayList<>();

        try {

            List<String> lineas = Files.readAllLines(Paths.get(LOG_FILE));

            for (String linea : lineas) {

                // SOLO mostrar líneas de auditoría
                if (!linea.contains("AUDITORIA |")) {
                    continue;
                }

                AuditLog log = new AuditLog();

                // Fecha
                String fecha = linea.substring(0,19);
                log.setFecha(LocalDateTime.parse(fecha, FORMATTER));

                // Nivel
                if(linea.contains(" INFO "))
                    log.setNivel("INFO");
                else if(linea.contains("WARN"))
                    log.setNivel("WARN");
                else if(linea.contains("ERROR"))
                    log.setNivel("ERROR");
                else
                    log.setNivel("DEBUG");

                // Módulo
                String modulo = "-";

                if (linea.contains("Módulo=")) {
                    int ini = linea.indexOf("Módulo=") + "Módulo=".length();
                    int fin = linea.indexOf("| Usuario");
                    modulo = linea.substring(ini, fin).trim();
                }

                log.setModulo(modulo);

                // Usuario
                String usuario="-";

                if(linea.contains("Usuario=")){
                    int ini=linea.indexOf("Usuario=")+8;
                    int fin=linea.indexOf("| Acción");
                    usuario=linea.substring(ini,fin).trim();
                }

                log.setUsuario(usuario);

                // Acción
                String accion="";

                if(linea.contains("Acción=")){
                    int ini=linea.indexOf("Acción=")+7;
                    accion=linea.substring(ini).trim();
                }

                log.setAccion(accion);

                lista.add(log);

            }

        } catch(IOException e){

            e.printStackTrace();

        }
        // ordenar mas reciente, mas antiguo
        lista.sort(Comparator.comparing(AuditLog::getFecha).reversed());

        return lista;

    }
    @Override
    public long contarInfo() {
        return obtenerLogs().stream()
                .filter(log -> "INFO".equals(log.getNivel()))
                .count();
    }
    @Override
    public long contarWarn() {
        return obtenerLogs().stream()
                .filter(log -> "WARN".equals(log.getNivel()))
                .count();
    }
    @Override
    public long contarError() {
        return obtenerLogs().stream()
                .filter(log -> "ERROR".equals(log.getNivel()))
                .count();
    }
    

}