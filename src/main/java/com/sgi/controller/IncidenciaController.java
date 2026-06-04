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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador encargado de gestionar las operaciones y vistas relacionadas con las incidencias.
 * Maneja tanto el flujo del docente como el flujo de Soporte TI.
 */
@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    /**
     * Constructor por defecto.
     */
    public IncidenciaController() {}
    
    @Autowired
    private IncidenciaService incidenciaService;

    // ==========================================
    // RUTAS DEL PANEL DE DOCENTE
    // ==========================================

    /**
     * Muestra el panel principal del docente con el listado de sus incidencias.
     * @param session La sesión HTTP actual para verificar el usuario logueado.
     * @param model El modelo para pasar datos a la vista.
     * @return La vista del panel del docente o redirección si no tiene permisos.
     */
    @GetMapping("/mis-incidencias")
    public String verPanelDocente(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        
        // CANDADO: Si es "soporte ti" o "tecnico", lo regresamos a su área
        String rol = usuarioLogueado.getRol().trim();
        if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
            return "redirect:/incidencias/panel-tecnico";
        }

        List<Incidencia> misIncidencias = incidenciaService.obtenerPorUsuario(usuarioLogueado);

        long pendientes = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA")).count();

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
     * @return La vista del formulario de nueva incidencia.
     */
    @GetMapping("/nueva")
    public String mostrarFormularioIncidencia(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        
        // CANDADO EXTRA: Evitar que el técnico entre a crear reportes
        String rol = usuarioLogueado.getRol().trim();
        if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
            return "redirect:/incidencias/panel-tecnico";
        }

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        return "nueva-incidencia"; 
    }

    /**
     * Procesa y guarda una nueva incidencia en la base de datos.
     * @param tipoIncidencia Descripción del problema.
     * @param categoria Categoría técnica del problema.
     * @param prioridad Nivel de urgencia.
     * @param ubicacion Lugar físico de la incidencia.
     * @param session La sesión HTTP actual.
     * @return Redirección al panel del docente.
     */
    @PostMapping("/guardar")
    public String guardarIncidencia(
            @RequestParam("tipoIncidencia") String tipoIncidencia,
            @RequestParam("categoria") String categoria,
            @RequestParam("prioridad") String prioridad,
            @RequestParam("ubicacion") String ubicacion,
            HttpSession session) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        Incidencia nueva = new Incidencia();
        nueva.setTipoIncidencia(tipoIncidencia);
        nueva.setCategoria(categoria);
        nueva.setPrioridad(prioridad);
        nueva.setUbicacion(ubicacion);
        nueva.setUsuario(usuarioLogueado);
        
        incidenciaService.guardar(nueva); 

        return "redirect:/incidencias/mis-incidencias";
    }
    
    // ==========================================
    // RUTAS DEL PANEL DE TI (SOPORTE TÉCNICO)
    // ==========================================

    /**
     * Muestra el panel de control (dashboard) para el personal de Soporte TI.
     * @param session La sesión HTTP actual.
     * @param model El modelo para pasar datos a la vista.
     * @return La vista del panel técnico.
     */
    @GetMapping("/panel-tecnico")
    public String verPanelTecnico(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";
        
        String rol = usuarioLogueado.getRol().trim();
        if (!rol.equalsIgnoreCase("soporte ti") && !rol.equalsIgnoreCase("tecnico") && !rol.equalsIgnoreCase("administrador")) {
            return "redirect:/incidencias/mis-incidencias"; 
        }

        List<Incidencia> todasIncidencias = incidenciaService.obtenerTodas();

        long pendientes = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA")).count();

        List<Incidencia> activas = todasIncidencias.stream()
                .filter(i -> !i.getEstado().equalsIgnoreCase("RESUELTA"))
                .sorted((i1, i2) -> Integer.compare(obtenerPesoPrioridad(i1.getPrioridad()), obtenerPesoPrioridad(i2.getPrioridad())))
                .toList();

        List<Incidencia> historialResueltas = todasIncidencias.stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA"))
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
     * @param idIncidencia Identificador de la incidencia a modificar.
     * @param nuevoEstado El nuevo estado a asignar (ej. EN PROCESO, RESUELTA).
     * @param archivo Archivo de evidencia física (opcional).
     * @return Redirección al panel técnico.
     */
    @PostMapping("/actualizar-estado")
    public String actualizarEstado(
            @RequestParam("idIncidencia") Integer idIncidencia, 
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam(value = "archivoEvidencia", required = false) MultipartFile archivo) {
        
        Incidencia incidencia = incidenciaService.obtenerPorId(idIncidencia); 
        
        if (incidencia != null) {
            incidencia.setEstado(nuevoEstado);
            
            // NUEVO: GUARDAR FECHA Y HORA DE RESOLUCIÓN  
            if (nuevoEstado.equals("RESUELTA")) {
                incidencia.setFechaCierre(java.time.LocalDateTime.now());
            }  
            
            // LÓGICA DE SUBIDA DE EVIDENCIA
            if (nuevoEstado.equals("RESUELTA") && archivo != null && !archivo.isEmpty()) {
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
     * Calcula el peso numérico de una prioridad para facilitar su ordenamiento.
     * @param prioridad Texto de la prioridad (ALTA, MEDIA, BAJA).
     * @return Valor numérico (1 para ALTA, 2 para MEDIA, 3 para otros).
     */
    private int obtenerPesoPrioridad(String prioridad) {
        if (prioridad == null) return 3;
        if (prioridad.equalsIgnoreCase("ALTA")) return 1;
        if (prioridad.equalsIgnoreCase("MEDIA")) return 2;
        return 3;
    }
    
    // ==========================================
    // API PARA AJAX POLLING (NOTIFICACIONES)
    // ==========================================
    
    /**
     * Endpoint API para consultar silenciosamente el total de tickets pendientes.
     * @param session La sesión HTTP actual.
     * @return El conteo total de incidencias en estado PENDIENTE.
     */
    @GetMapping("/api/conteo-pendientes")
    @ResponseBody
    public long contarIncidenciasPendientes(HttpSession session) {
        return incidenciaService.obtenerTodas().stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE"))
                .count();
    }

}