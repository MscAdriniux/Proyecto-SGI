package com.sgi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        // Establecer un timeout de 30 minutos (1800000 ms)
        SseEmitter emitter = new SseEmitter(1800000L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // Enviar evento inicial de conexión
        try {
            emitter.send(SseEmitter.event().name("CONNECT").data("Conexión de notificaciones SGI establecida."));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void notifyNewIncident(String tipoIncidencia, String ubicacion, String prioridad) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        
        String jsonPayload = String.format(
            "{\"tipo\":\"%s\",\"ubicacion\":\"%s\",\"prioridad\":\"%s\"}",
            tipoIncidencia.replace("\"", "\\\""),
            ubicacion.replace("\"", "\\\""),
            prioridad
        );
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NUEVA_INCIDENCIA")
                        .data(jsonPayload));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        
        emitters.removeAll(deadEmitters);
    }
}
