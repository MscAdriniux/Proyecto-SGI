package com.sgi.controller;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.service.FileStorageService;
import com.sgi.service.IncidenciaService;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador encargado exclusivamente de las vistas y acciones del rol
 * Técnico / Soporte TI: ver su panel de tickets y actualizar el estado de una incidencia.
 */
@Controller
public class TecnicoController {

    private static final Logger logger = LoggerFactory.getLogger(TecnicoController.class);

    public TecnicoController() {}

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/incidencias/panel-tecnico")
    public String verPanelTecnico(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        String rol = usuarioLogueado.getRol().trim();
        if (!rol.equalsIgnoreCase("soporte ti") && !rol.equalsIgnoreCase("tecnico") && !rol.equalsIgnoreCase("administrador")) {
            return "redirect:/incidencias/panel-docente";
        }

        String nombreTecnico = usuarioLogueado.getNombres() + " " + usuarioLogueado.getApellidos();
        List<Incidencia> misIncidencias = incidenciaService.obtenerIncidenciasParaTecnico(nombreTecnico);

        long pendientes = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA") || i.getEstado().equalsIgnoreCase("ATENDIDA")).count();

        List<Incidencia> listaUnificada = incidenciaService.obtenerOrdenadasPorPrioridad(misIncidencias);

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("incidencias", listaUnificada);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-tecnico";
    }

    @PostMapping("/incidencias/actualizar-estado")
    public String actualizarEstado(
            @RequestParam("idIncidencia") Integer idIncidencia,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam(value = "archivoEvidencia", required = false) MultipartFile archivo,
            HttpSession session) {

        Usuario tecnico = (Usuario) session.getAttribute("usuarioLogueado");
        if (tecnico == null) return "redirect:/login";

        Incidencia incidencia = incidenciaService.obtenerPorId(idIncidencia);

        if (incidencia != null) {
            incidencia.setEstado(nuevoEstado);

            if (incidencia.getAsignadoA() == null) {
                incidencia.setAsignadoA(tecnico.getNombres() + " " + tecnico.getApellidos());
            }

            if (nuevoEstado.equals("RESUELTA") || nuevoEstado.equals("ATENDIDA")) {
                incidencia.setFechaCierre(LocalDateTime.now());
            }

            if ((nuevoEstado.equals("RESUELTA") || nuevoEstado.equals("ATENDIDA")) && archivo != null && !archivo.isEmpty()) {
                String nombreFoto = fileStorageService.guardarEvidencia(archivo, idIncidencia);
                if (nombreFoto != null) {
                    incidencia.setEvidenciaUrl(nombreFoto);
                }
            }

            incidenciaService.guardar(incidencia);

            logger.info(
                "AUDITORIA | Módulo=Gestión de Incidencias | Usuario={} | Acción=Cambió estado | Ticket={} | Estado={}",
                tecnico.getCorreo(),
                incidencia.getIdIncidencia(),
                nuevoEstado
            );
        }

        return "redirect:/incidencias/panel-tecnico";
    }
}