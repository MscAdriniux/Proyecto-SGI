package com.sgi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    // Estructura interna para identificar de forma unívoca cada conexión abierta
    private static class SseClient {
        final SseEmitter emitter;
        final Integer idUsuario;
        final String rol;
        final String nombreCompleto;

        SseClient(SseEmitter emitter, Integer idUsuario, String rol, String nombreCompleto) {
            this.emitter = emitter;
            this.idUsuario = idUsuario;
            this.rol = rol != null ? rol.trim().toLowerCase() : "";
            this.nombreCompleto = nombreCompleto != null ? nombreCompleto.trim().toLowerCase() : "";
        }
    }

    private final List<SseClient> clients = new CopyOnWriteArrayList<>();

    /**
     * Registra de manera persistente una conexión SSE asociándola a la identidad del usuario logueado.
     */
    public SseEmitter subscribe(Integer idUsuario, String rol, String nombreCompleto) {
        SseEmitter emitter = new SseEmitter(1800000L); // Timeout de 30 minutos
        SseClient client = new SseClient(emitter, idUsuario, rol, nombreCompleto);
        clients.add(client);

        emitter.onCompletion(() -> clients.remove(client));
        emitter.onTimeout(() -> clients.remove(client));
        emitter.onError((e) -> clients.remove(client));

        try {
            emitter.send(SseEmitter.event().name("CONNECT").data("Conexión de notificaciones SGI establecida de forma segura."));
        } catch (IOException e) {
            clients.remove(client);
        }

        return emitter;
    }

    /**
     * Distribuye las alertas garantizando la privacidad e independencia entre usuarios del mismo rol.
     * * @param mensaje Texto estructurado con prefijo que viajará al cliente.
     * @param idDocente ID del docente creador del ticket (para actualizar su panel).
     * @param nombreTecnicoAsignado Nombre completo del técnico a cargo (para aislar sus procesos).
     * @param enviarATodosLosTecnicos True si es una alerta global de ticket nuevo para la bolsa de trabajo técnico.
     */
    public void broadcastSegmentado(String mensaje, Integer idDocente, String nombreTecnicoAsignado, boolean enviarATodosLosTecnicos) {
        List<SseClient> deadClients = new ArrayList<>();
        String nombreTecnicoNorm = nombreTecnicoAsignado != null ? nombreTecnicoAsignado.trim().toLowerCase() : "";

        for (SseClient client : clients) {
            boolean debeRecibir = false;

            // Regla 1: Los Administradores siempre reciben absolutamente todos los eventos del sistema
            if (client.rol.equals("administrador") || client.rol.equals("admin")) {
                debeRecibir = true;
            }
            
            // Regla 2: El Docente dueño específico del ticket recibe la alerta para actualizar sus tarjetas
            if (idDocente != null && client.idUsuario.equals(idDocente)) {
                debeRecibir = true;
            }

            // Regla 3: Si es un reporte NUEVO, se le notifica a toda la bolsa de técnicos para que puedan tomarlo
            if (enviarATodosLosTecnicos && (client.rol.equals("soporte ti") || client.rol.equals("tecnico") || client.rol.equals("ti"))) {
                debeRecibir = true;
            }

            // Regla 4: Si el proceso pertenece a un técnico asignado, solo ese técnico específico intercepta la alerta
            if (!nombreTecnicoNorm.isEmpty() && client.nombreCompleto.equals(nombreTecnicoNorm)) {
                debeRecibir = true;
            }

            if (debeRecibir) {
                try {
                    client.emitter.send(SseEmitter.event().data(mensaje));
                } catch (IOException e) {
                    deadClients.add(client);
                }
            }
        }
        clients.removeAll(deadClients);
    }
}