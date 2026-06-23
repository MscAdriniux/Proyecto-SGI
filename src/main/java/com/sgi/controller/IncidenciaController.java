package com.sgi.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Controlador principal para la gestión de incidencias.
 * Unifica los flujos de Docentes, Soporte TI y Administradores, integrando
 * notificaciones en tiempo real y exportación de reportes.
 */
@Controller
public class IncidenciaController {

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

    // ==========================================
    // 1. VISTAS DEL DOCENTE
    // ==========================================

    /**
     * Muestra el panel principal del docente con el listado de sus incidencias.
     * @param session La sesión HTTP actual.
     * @param model El modelo para pasar datos a la vista.
     * @return La vista del panel del docente.
     */
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
    
    /**
     * Muestra el formulario para crear un nuevo ticket de incidencia.
     * @param session La sesión HTTP actual.
     * @param model El modelo para pasar datos a la vista.
     * @return La vista del formulario.
     */
    @GetMapping("/incidencias/nueva")
    public String mostrarFormularioIncidencia(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        
        String rol = usuarioLogueado.getRol().trim();
        if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
            return "redirect:/incidencias/panel-tecnico";
        }

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        return "nueva-incidencia"; 
    }

    /**
     * Valida, procesa y guarda una nueva incidencia, emitiendo una notificación en tiempo real.
     * @param tipoIncidencia Descripción del problema.
     * @param categoria Categoría técnica.
     * @param prioridad Nivel de urgencia.
     * @param ubicacion Lugar del incidente.
     * @param session La sesión HTTP actual.
     * @param model El modelo para errores.
     * @return Redirección al panel del docente.
     */
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

        return "redirect:/incidencias/panel-docente";
    }

    // ==========================================
    // 2. VISTAS Y ACCIONES DEL ADMINISTRADOR
    // ==========================================

    /**
     * Muestra el panel de control exclusivo para administradores.
     * @param session Sesión actual.
     * @param model Modelo para la vista.
     * @return Vista del panel administrador.
     */
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

        return "panel-admin";
    }

    /**
     * Genera y descarga un reporte en formato Excel de todas las incidencias.
     * @param session Sesión actual.
     * @return Archivo Excel descargable.
     * @throws IOException Si ocurre un error al generar el archivo.
     */
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
    // 4. RUTAS DEL PANEL TÉCNICO ORIGINAL (SOPORTE)
    // ==========================================

    /**
     * Muestra el panel técnico con funcionalidades de ordenamiento por prioridad.
     * @param session La sesión HTTP actual.
     * @param model El modelo para la vista.
     * @return La vista del panel técnico original.
     */
    @GetMapping("/incidencias/panel-tecnico")
    public String verPanelTecnico(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        
        String rol = usuarioLogueado.getRol().trim();
        if (!rol.equalsIgnoreCase("soporte ti") && !rol.equalsIgnoreCase("tecnico") && !rol.equalsIgnoreCase("administrador")) {
            return "redirect:/incidencias/panel-docente"; 
        }

        List<Incidencia> todasIncidencias = incidenciaService.obtenerTodas();

        long pendientes = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA") || i.getEstado().equalsIgnoreCase("ATENDIDA")).count();

        List<Incidencia> activas = todasIncidencias.stream()
                .filter(i -> !i.getEstado().equalsIgnoreCase("RESUELTA") && !i.getEstado().equalsIgnoreCase("ATENDIDA"))
                .sorted((i1, i2) -> Integer.compare(obtenerPesoPrioridad(i1.getPrioridad()), obtenerPesoPrioridad(i2.getPrioridad())))
                .toList();

        List<Incidencia> historialResueltas = todasIncidencias.stream()
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

    /**
     * Actualiza el estado de una incidencia y gestiona la subida de evidencia fotográfica.
     * @param idIncidencia Identificador de la incidencia.
     * @param nuevoEstado El nuevo estado a asignar.
     * @param archivo Archivo de evidencia física.
     * @return Redirección al panel técnico.
     */
    @PostMapping("/incidencias/actualizar-estado")
    public String actualizarEstado(
            @RequestParam("idIncidencia") Integer idIncidencia, 
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam(value = "archivoEvidencia", required = false) MultipartFile archivo) {
        
        Incidencia incidencia = incidenciaService.obtenerPorId(idIncidencia); 
        
        if (incidencia != null) {
            incidencia.setEstado(nuevoEstado);
            
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
                    System.out.println("Error fatal al subir la foto de evidencia: " + e.getMessage());
                }
            }

            incidenciaService.guardar(incidencia);
        }
        
        return "redirect:/incidencias/panel-tecnico";
    }
    
    /**
     * Calcula el peso numérico de una prioridad.
     * @param prioridad Texto de la prioridad.
     * @return Valor numérico.
     */
    private int obtenerPesoPrioridad(String prioridad) {
        if (prioridad == null) return 3;
        if (prioridad.equalsIgnoreCase("ALTA")) return 1;
        if (prioridad.equalsIgnoreCase("MEDIA")) return 2;
        return 3;
    }
    
    // ==========================================
    // 5. API Y NOTIFICACIONES
    // ==========================================

    /**
     * Suscripción SSE (Server-Sent Events) para recibir notificaciones en vivo.
     * @param session Sesión actual.
     * @return Objeto SseEmitter para mantener la conexión abierta.
     */
    @GetMapping("/api/notificaciones/suscripcion")
    public SseEmitter suscribirNotificaciones(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return null;
        }
        return notificationService.subscribe();
    }
    
    /**
     * Endpoint API clásico para consultar el total de tickets pendientes (Polling).
     * @param session La sesión HTTP actual.
     * @return El conteo total de incidencias PENDIENTES.
     */
    @GetMapping("/api/conteo-pendientes")
    @ResponseBody
    public long contarIncidenciasPendientes(HttpSession session) {
        return incidenciaService.obtenerTodas().stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE"))
                .count();
    }
}