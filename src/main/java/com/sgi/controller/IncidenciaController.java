package com.sgi.controller;
import com.sgi.service.ExcelService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private ExcelService excelService;

    // ==========================================
    // RUTAS DEL PANEL DE DOCENTE
    // ==========================================

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
                    
                } catch (IOException | IllegalArgumentException e) { // Cambiado a Exception general por si hay errores de rutas
                    System.out.println("Error fatal al subir la foto de evidencia: " + e.getMessage());
                }
            }

            incidenciaService.guardar(incidencia);
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
    // API PARA AJAX POLLING (NOTIFICACIONES)
    // ==========================================
    @GetMapping("/api/conteo-pendientes")
    @ResponseBody
    public long contarIncidenciasPendientes(HttpSession session) {
        // Obtenemos todas y contamos las pendientes (igual que en el panel)
        return incidenciaService.obtenerTodas().stream()
                .filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE"))
                .count();
    }

    @PutMapping("/{id}/estado")
    public Incidencia cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
    
        return incidenciaService.cambiarEstado(id, estado);
    }
    
    @PutMapping("/{id}/comentario")
    public Incidencia agregarComentario(
            @PathVariable Long id,
            @RequestParam String comentario) {
    
        return incidenciaService.agregarComentario(id, comentario);
    }

    @GetMapping("/estado/{estado}")
    public List<Incidencia> obtenerPorEstado(@PathVariable String estado) {
        return incidenciaService.obtenerPorEstado(estado);
    }

    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel() {
    
        List<Incidencia> incidencias = incidenciaService.obtenerTodas();
    
        byte[] excel = excelService.generarReporte(incidencias);
    
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=incidencias.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
