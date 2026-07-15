package com.sgi.controller;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.service.IncidenciaService;
import com.sgi.service.NotificationService;
import com.sgi.service.ReporteService;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador encargado exclusivamente de las vistas y acciones del rol
 * Administrador sobre las incidencias: panel general, exportación a Excel
 * y resolución/escalamiento definitivo de tickets.
 */
@Controller
public class AdminIncidenciaController {

    private static final Logger logger = LoggerFactory.getLogger(AdminIncidenciaController.class);

    public AdminIncidenciaController() {}

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReporteService reporteservice;

    @GetMapping("/admin/panel-admin")
    public String verPanelAdmin(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        List<Incidencia> todas = incidenciaService.obtenerOrdenadasPorPrioridad(
            incidenciaService.obtenerTodas()
        );

        long pendientes = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = todas.stream().filter(i -> i.getEstado().equalsIgnoreCase("ATENDIDA") || i.getEstado().equalsIgnoreCase("RESUELTA")).count();

        model.addAttribute("usuarioLogueado", u);
        model.addAttribute("incidencias", todas);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        logger.info(
            "AUDITORIA | Módulo=Panel Administrador | Usuario={} | Acción=Ingresó al Panel Administrador",
            u.getCorreo()
        );

        return "panel-admin";
    }

    @GetMapping("/admin/reporte/excel")
    public ResponseEntity<byte[]> descargarExcel(HttpSession session) throws IOException {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return ResponseEntity.status(403).build();
        }

        List<Incidencia> todas = incidenciaService.obtenerTodas();
        byte[] excelBytes = reporteservice.generarReporte(todas);

        logger.info(
            "AUDITORIA | Módulo=Reportes | Usuario={} | Acción=Exportó reporte Excel",
            u.getCorreo()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Incidencias.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    @PostMapping("/admin/resolver-incidencia")
    public String adminResolverIncidencia(@RequestParam("idIncidencia") Integer idIncidencia, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        if (admin == null || !admin.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        Incidencia incidencia = incidenciaService.obtenerPorId(idIncidencia);

        if (incidencia != null && incidencia.getEstado().equalsIgnoreCase("ATENDIDA")) {
            incidencia.setEstado("RESUELTA");
            incidencia.setFechaCierre(LocalDateTime.now());
            incidencia.setComentarioAdmin("Cierre definitivo por el Administrador (Liberado de la bandeja de escalamiento).");
            incidenciaService.guardar(incidencia);

            Integer idDocente = incidencia.getUsuario().getIdUsuario();
            String nombreTecnico = incidencia.getAsignadoA();

            notificationService.broadcastSegmentado(
                "FINAL: El Administrador ha RESUELTO y cerrado definitivamente la incidencia escalada de '" + incidencia.getTipoIncidencia() + "' en " + incidencia.getUbicacion() + " (Ticket #" + incidencia.getIdIncidencia() + "). El aula queda liberada.",
                idDocente,
                nombreTecnico,
                false
            );
        }

        return "redirect:/admin/panel-admin";
    }
}