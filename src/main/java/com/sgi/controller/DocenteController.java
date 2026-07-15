package com.sgi.controller;

import com.sgi.model.Aula;
import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.AulaRepository;
import com.sgi.service.IncidenciaService;
import com.sgi.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador encargado exclusivamente de las vistas y acciones del rol Docente:
 * ver su panel, reportar una nueva incidencia y guardarla.
 */
@Controller
public class DocenteController {

    private static final Logger logger = LoggerFactory.getLogger(DocenteController.class);

    public DocenteController() {}

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AulaRepository aulaRepository;

    @GetMapping("/incidencias/panel-docente")
    public String verPanelDocente(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        String rol = usuarioLogueado.getRol().trim();
        if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
            return "redirect:/incidencias/panel-tecnico";
        }

        List<Incidencia> misIncidencias = incidenciaService.obtenerOrdenadasPorPrioridad(
            incidenciaService.obtenerPorUsuario(usuarioLogueado)
        );

        long pendientes = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA") || i.getEstado().equalsIgnoreCase("ATENDIDA")).count();

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("incidencias", misIncidencias);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-docente";
    }

    @GetMapping("/incidencias/nueva")
    public String mostrarFormularioIncidencia(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        String rol = usuarioLogueado.getRol().trim();
        if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
            return "redirect:/incidencias/panel-tecnico";
        }

        List<Aula> listaAulas = aulaRepository.findAll();

        model.addAttribute("aulas", listaAulas);
        model.addAttribute("usuarioLogueado", usuarioLogueado);

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

        // VALIDACIÓN DE DUPLICADOS EN LA MISMA UBICACIÓN SOBRE LA MISMA INCIDENCIA
        if (incidenciaService.existeActivaEnUbicacion(ubicacion, tipoIncidencia)) {
            String mensajeError = "Ya existe un reporte activo para la incidencia '" + tipoIncidencia + "' en la ubicación '" + ubicacion + "'.";

            if (incidenciaService.esIncidenciaAtendida(ubicacion, tipoIncidencia)) {
                mensajeError = "La incidencia '" + tipoIncidencia + "' en la ubicación '" + ubicacion + "' ya fue ATENDIDA por soporte técnico pero aún no está completamente RESUELTA. Por favor, comuníquese con el Administrador de Soporte TI al +51 987 654 321 para mayor información.";
            }

            model.addAttribute("error", mensajeError);
            model.addAttribute("usuarioLogueado", usuarioLogueado);
            model.addAttribute("aulas", aulaRepository.findAll());
            return "nueva-incidencia";
        }

        Incidencia nueva = new Incidencia();
        nueva.setTipoIncidencia(tipoIncidencia);
        nueva.setCategoria(categoria);
        nueva.setPrioridad(prioridad);
        nueva.setUbicacion(ubicacion);
        nueva.setUsuario(usuarioLogueado);

        incidenciaService.guardar(nueva);

        logger.info(
            "AUDITORIA | Módulo=Gestión de Incidencias | Usuario={} | Acción=Registró incidencia | Tipo={} | Ubicación={}",
            usuarioLogueado.getCorreo(),
            tipoIncidencia,
            ubicacion
        );

        notificationService.broadcastSegmentado(
            "NUEVO: Se ha reportado una nueva incidencia de '" + nueva.getTipoIncidencia() + "' en la ubicación " + nueva.getUbicacion() + " (Prioridad: " + nueva.getPrioridad() + "). Creado por: " + usuarioLogueado.getNombres() + " " + usuarioLogueado.getApellidos() + ".",
            usuarioLogueado.getIdUsuario(),
            null,
            true
        );

        return "redirect:/incidencias/panel-docente";
    }
}