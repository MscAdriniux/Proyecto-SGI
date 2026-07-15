/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sgi.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author ad_ri
 */
public interface NotificationService {

    /**
     * Distribuye las alertas garantizando la privacidad e independencia entre usuarios del mismo rol.
     * * @param mensaje Texto estructurado con prefijo que viajará al cliente.
     * @param idDocente ID del docente creador del ticket (para actualizar su panel).
     * @param nombreTecnicoAsignado Nombre completo del técnico a cargo (para aislar sus procesos).
     * @param enviarATodosLosTecnicos True si es una alerta global de ticket nuevo para la bolsa de trabajo técnico.
     */
    void broadcastSegmentado(String mensaje, Integer idDocente, String nombreTecnicoAsignado, boolean enviarATodosLosTecnicos);

    /**
     * Registra de manera persistente una conexión SSE asociándola a la identidad del usuario logueado.
     */
    SseEmitter subscribe(Integer idUsuario, String rol, String nombreCompleto);
    
}
