package com.sgi.service;

import com.sgi.model.AuditLog;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {

    private static final String LOG_FILE = "logs/sgi.log";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<AuditLog> obtenerLogs() {

        List<AuditLog> lista = new ArrayList<>();

        Path path = Paths.get(LOG_FILE);

        if (!Files.exists(path)) {
            return lista;
        }

        try {

            List<String> lineas = Files.readAllLines(path);

            for (String linea : lineas) {

                if (linea.length() < 20) {
                    continue;
                }

                try {

                    String fechaTexto = linea.substring(0, 19);

                    LocalDateTime fecha =
                            LocalDateTime.parse(fechaTexto, FORMATTER);

                    String nivel = "";

                    if (linea.contains(" INFO ")) {
                        nivel = "INFO";
                    } else if (linea.contains(" WARN ")) {
                        nivel = "WARN";
                    } else if (linea.contains(" ERROR ")) {
                        nivel = "ERROR";
                    } else if (linea.contains(" DEBUG ")) {
                        nivel = "DEBUG";
                    }

                    String modulo = "";

                    int inicioLogger = linea.indexOf(nivel);

                    if (inicioLogger > 0) {

                        String resto = linea.substring(inicioLogger + nivel.length());

                        int guion = resto.indexOf("-");

                        if (guion > 0) {

                            modulo = resto.substring(0, guion).trim();

                        }

                    }

                    String accion = "";

                    int indice = linea.indexOf("-");

                    if (indice > 0) {

                        accion = linea.substring(indice + 1).trim();

                    }

                    AuditLog log = new AuditLog();

                    log.setFecha(fecha);
                    log.setNivel(nivel);
                    log.setModulo(modulo);
                    log.setUsuario("-");
                    log.setAccion(accion);

                    lista.add(log);

                } catch (Exception ex) {

                    // Ignorar líneas mal formadas

                }

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        return lista;

    }

}