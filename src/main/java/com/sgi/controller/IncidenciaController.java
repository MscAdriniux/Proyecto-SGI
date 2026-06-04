package com.sgi.controller;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.service.IncidenciaService;
import com.sgi.service.NotificationService;
import com.sgi.service.ExcelReportService;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class IncidenciaController {

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ExcelReportService excelReportService;

    // ==========================================
    // 1. VISTAS DEL DOCENTE
    // ==========================================

    @GetMapping("/incidencias/mis-incidencias")
    public String verPanelDocente(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        List<Incidencia> misIncidencias = incidenciaService.obtenerPorUsuario(usuarioLogueado);

        long pendientes = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("ATENDIDA")).count();

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("incidencias", misIncidencias);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-docente";
    }
    
    @GetMapping("/incidencias/nueva")
    public String mostrarFormularioIncidencia(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        return "nueva-incidencia";
    }

    @PostMapping("/incidencias/guardar")
    public String guardarIncidencia(
            @RequestParam("tipoIncidencia") String tipoIncidencia,
            @RequestParam("categoria") String categoria,
            @RequestParam("prioridad") String prioridad,
            @RequestParam("ubicacion") String ubicacion,
            HttpSession session,
            Model model) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        // VALIDACIÓN DE DUPLICADOS EN LA MISMA UBICACIÓN
        if (incidenciaService.existeActivaEnUbicacion(ubicacion, tipoIncidencia)) {
            model.addAttribute("error", "Ya existe un reporte activo para la incidencia '" + tipoIncidencia + "' en la ubicación '" + ubicacion + "'.");
            model.addAttribute("usuarioLogueado", usuarioLogueado);
            return "nueva-incidencia";
        }

        Incidencia nueva = new Incidencia();
        nueva.setTipoIncidencia(tipoIncidencia);
        nueva.setCategoria(categoria);
        nueva.setPrioridad(prioridad);
        nueva.setUbicacion(ubicacion);
        nueva.setUsuario(usuarioLogueado);
        
        incidenciaService.guardar(nueva); 

        // Enviar notificación en tiempo real a los técnicos de TI
        notificationService.notifyNewIncident(nueva.getTipoIncidencia(), nueva.getUbicacion(), nueva.getPrioridad());

        return "redirect:/incidencias/mis-incidencias";
    }

    // ==========================================
    // 2. VISTAS Y ACCIONES DEL ADMINISTRADOR
    // ==========================================

    @GetMapping("/admin/dashboard")
    public String verPanelAdmin(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        List<Incidencia> todas = incidenciaService.obtenerTodas();

        long pendientes = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("ATENDIDA")).count();

        model.addAttribute("usuarioLogueado", u);
        model.addAttribute("incidencias", todas);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-admin";
    }

    @GetMapping("/admin/reporte/excel")
    public ResponseEntity<byte[]> descargarExcel(HttpSession session) throws IOException {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return ResponseEntity.status(403).build();
        }

        List<Incidencia> todas = incidenciaService.obtenerTodas();
        byte[] excelBytes = excelReportService.generarReporteIncidencias(todas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Incidencias.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    // ==========================================
    // 3. VISTAS Y ACCIONES DEL TÉCNICO DE TI
    // ==========================================

    @GetMapping("/ti/dashboard")
    public String verPanelTI(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || (!u.getRol().equalsIgnoreCase("ti") && !u.getRol().equalsIgnoreCase("soporte ti"))) {
            return "redirect:/login";
        }

        List<Incidencia> todas = incidenciaService.obtenerTodas();

        long pendientes = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("ATENDIDA") || i.getEstado().equalsIgnoreCase("RECHAZADA")).count();

        model.addAttribute("usuarioLogueado", u);
        model.addAttribute("incidencias", todas);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-ti";
    }

    @PostMapping("/ti/asignar")
    public String tomarIncidencia(@RequestParam("id") Integer id, HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || (!u.getRol().equalsIgnoreCase("ti") && !u.getRol().equalsIgnoreCase("soporte ti"))) {
            return "redirect:/login";
        }

        Incidencia inc = incidenciaService.obtenerPorId(id);
        if (inc != null && inc.getEstado().equalsIgnoreCase("PENDIENTE")) {
            inc.setEstado("EN PROCESO");
            inc.setAsignadoA(u.getNombres() + " " + u.getApellidos());
            incidenciaService.guardar(inc);
        }

        return "redirect:/ti/dashboard";
    }

    @PostMapping("/ti/resolver")
    public String resolverIncidencia(
            @RequestParam("id") Integer id,
            @RequestParam("estado") String nuevoEstado,
            @RequestParam("resolucion") String resolucion,
            HttpSession session) {
        
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || (!u.getRol().equalsIgnoreCase("ti") && !u.getRol().equalsIgnoreCase("soporte ti"))) {
            return "redirect:/login";
        }

        Incidencia inc = incidenciaService.obtenerPorId(id);
        if (inc != null) {
            inc.setEstado(nuevoEstado.toUpperCase()); // "ATENDIDA" o "RECHAZADA"
            inc.setResolucion(resolucion);
            inc.setFechaCierre(LocalDateTime.now());
            inc.setAsignadoA(u.getNombres() + " " + u.getApellidos()); // Asegurar asignación
            incidenciaService.guardar(inc);
        }

        return "redirect:/ti/dashboard";
    }

    // Suscripción SSE para recibir notificaciones de incidencias
    @GetMapping("/api/notificaciones/suscripcion")
    public SseEmitter suscribirNotificaciones(HttpSession session) {
        // Permitir suscripción solo a usuarios logueados (idealmente TI, pero permitimos general)
        if (session.getAttribute("usuarioLogueado") == null) {
            return null;
        }
        return notificationService.subscribe();
    }
}