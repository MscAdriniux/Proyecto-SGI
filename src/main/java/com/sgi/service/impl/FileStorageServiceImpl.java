package com.sgi.service.impl;

import com.sgi.service.FileStorageService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    
    // Definimos la ruta base de la carpeta
    private final Path rutaCarpeta = Paths.get("uploads/evidencias");

    @Override
    public String guardarEvidencia(MultipartFile archivo, Integer idIncidencia) {
        try {
            // Crea la carpeta si no existe
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }
            
            // Extrae extensión y genera nombre (ej: evidencia_ticket_5.jpg)
            String extension = FilenameUtils.getExtension(archivo.getOriginalFilename());
            String nombreFoto = "evidencia_ticket_" + idIncidencia + "." + extension;
            
            // Guarda el archivo reemplazando si ya existe
            Path rutaCompleta = rutaCarpeta.resolve(nombreFoto);
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
            
            return nombreFoto;
            
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Error fatal al subir la foto del ticket {}: {}", idIncidencia, e.getMessage());
            return null;
        }
    }
}