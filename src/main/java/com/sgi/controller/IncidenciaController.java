package com.sgi.controller;

import com.sgi.model.Aula;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.repository.AulaRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Controlador principal para la gestión de incidencias.
 * Unifica los flujos de Docentes, Soporte TI y Administradores, integrando
 * notificaciones en tiempo real y exportación de reportes.
 */
@Controller
public class IncidenciaController {
    private static final Logger logger =
        LoggerFactory.getLogger(IncidenciaController.class);

    /**
     * Constructor por defecto.
     */
    public IncidenciaController() {}
    
    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private AulaRepository aulaRepository; // <-- Inyectado correctamente en la parte superior

    // ==========================================
    // 1. VISTAS DEL DOCENTE
    // ==========================================

    @GetMapping("/incidencias/panel-docente")
    public String verPanelDocente(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        
        String rol = usuarioLogueado.getRol().trim();
        if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
            return "redirect:/incidencias/panel-tecnico";
        }

        List<Incidencia> misIncidencias = incidenciaService.obtenerPorUsuario(usuarioLogueado);

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

        // Recuperamos las aulas y pasamos al modelo
        List<Aula> listaAulas = aulaRepository.findAll();
        System.out.println("========== AULAS ENCONTRADAS EN MYSQL: " + listaAulas.size() + " ==========");
        
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

        // VALIDACIÓN DE DUPLICADOS EN LA MISMA UBICACIÓN
        if (incidenciaService.existeActivaEnUbicacion(ubicacion, tipoIncidencia)) {
            model.addAttribute("error", "Ya existe un reporte activo para la incidencia '" + tipoIncidencia + "' en la ubicación '" + ubicacion + "'.");
            model.addAttribute("usuarioLogueado", usuarioLogueado);
            model.addAttribute("aulas", aulaRepository.findAll()); // Recargamos las aulas si hay error
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

        // Enviar notificación en tiempo real a los técnicos de TI
        notificationService.notifyNewIncident(nueva.getTipoIncidencia(), nueva.getUbicacion(), nueva.getPrioridad());

        return "redirect:/incidencias/panel-docente";
    }

    // ==========================================
    // 2. VISTAS Y ACCIONES DEL ADMINISTRADOR
    // ==========================================

    @GetMapping("/admin/panel-admin")
    public String verPanelAdmin(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

      
        List<Incidencia> todas = incidenciaService.obtenerTodas();

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
        byte[] excelBytes = excelReportService.generarReporte(todas);
        
        logger.info(
            "AUDITORIA | Módulo=Reportes | Usuario={} | Acción=Exportó reporte Excel",
            u.getCorreo()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Incidencias.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    // ==========================================
    // 4. RUTAS DEL PANEL TÉCNICO
    // ==========================================

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

        List<Incidencia> activas = misIncidencias.stream()
                .filter(i -> !i.getEstado().equalsIgnoreCase("RESUELTA") && !i.getEstado().equalsIgnoreCase("ATENDIDA"))
                .sorted((i1, i2) -> Integer.compare(obtenerPesoPrioridad(i1.getPrioridad()), obtenerPesoPrioridad(i2.getPrioridad())))
                .toList();

        List<Incidencia> historialResueltas = misIncidencias.stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA") || i.getEstado().equalsIgnoreCase("ATENDIDA"))
                .sorted((i1, i2) -> i2.getIdIncidencia().compareTo(i1.getIdIncidencia()))
                .toList();

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("incidenciasActivas", activas);
        model.addAttribute("incidenciasResueltas", historialResueltas);
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
                try {
                    String extension = FilenameUtils.getExtension(archivo.getOriginalFilename());
                    String nombreFoto = "evidencia_ticket_" + idIncidencia + "." + extension;
                    
                    Path rutaCarpeta = Paths.get("uploads/evidencias");
                    if (!Files.exists(rutaCarpeta)) {
                        Files.createDirectories(rutaCarpeta);
                    }
                    
                    Path rutaCompleta = rutaCarpeta.resolve(nombreFoto);
                    Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
                    
                    incidencia.setEvidenciaUrl(nombreFoto);
                    
                } catch (IOException | IllegalArgumentException e) { 
                    System.out.println("Error fatal al subir la foto: " + e.getMessage());
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
    
    private int obtenerPesoPrioridad(String prioridad) {
        if (prioridad == null) return 3;
        if (prioridad.equalsIgnoreCase("ALTA")) return 1;
        if (prioridad.equalsIgnoreCase("MEDIA")) return 2;
        return 3;
    }
    
    // ==========================================
    // 5. API Y NOTIFICACIONES
    // ==========================================

    @GetMapping("/api/notificaciones/suscripcion")
    public SseEmitter suscribirNotificaciones(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return null;
        }
        return notificationService.subscribe();
    }
    
    @GetMapping("/api/conteo-pendientes")
    @ResponseBody
    public long contarIncidenciasPendientes(HttpSession session) {
        return incidenciaService.obtenerTodas().stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE"))
                .count();
    }
}