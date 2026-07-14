package com.sgi.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Guarda la foto de evidencia de un ticket en el disco del servidor.
     * @param archivo El archivo físico subido por el técnico.
     * @param idIncidencia El ID del ticket para nombrar el archivo.
     * @return El nombre final del archivo guardado, o null si hubo un error.
     */
    String guardarEvidencia(MultipartFile archivo, Integer idIncidencia);
}