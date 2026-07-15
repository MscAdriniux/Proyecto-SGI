package com.sgi.controller;

import com.sgi.model.Usuario;
import com.sgi.service.IncidenciaService;
import com.sgi.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController 
@RequestMapping("/api")
public class NotificacionController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IncidenciaService incidenciaService;

    @GetMapping("/notificaciones/suscripcion")
    public SseEmitter suscribirNotificaciones(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return null;
        
        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();
        return notificationService.subscribe(usuario.getIdUsuario(), usuario.getRol(), nombreCompleto);
    }
    
    @GetMapping("/conteo-pendientes")
    public long contarIncidenciasPendientes(HttpSession session) {
        return incidenciaService.obtenerTodas().stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE"))
                .count();
    }
}