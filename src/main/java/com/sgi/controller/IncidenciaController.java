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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    @Autowired
    private IncidenciaService incidenciaService;

    @GetMapping("/mis-incidencias")
    public String verPanelDocente(HttpSession session, Model model) {
        // 1. Verificamos que el usuario esté logueado
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login"; // Si no hay sesión, lo botamos al login
        }

        // 2. Buscamos sus incidencias
        List<Incidencia> misIncidencias = incidenciaService.obtenerPorUsuario(usuarioLogueado);

        // 3. Calculamos las estadísticas para las tarjetas superiores
        long pendientes = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = misIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA")).count();

        // 4. Mandamos los datos al HTML
        model.addAttribute("usuarioLogueado", usuarioLogueado); // <--- ESTA ES LA LÍNEA NUEVA
        model.addAttribute("incidencias", misIncidencias);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-docente";
    }
    
    // Mostrar el formulario
    @GetMapping("/nueva")
    public String mostrarFormularioIncidencia(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        return "nueva-incidencia"; // Busca nueva-incidencia.html
    }

    // Guardar el formulario
    @PostMapping("/guardar")
    public String guardarIncidencia(
            @RequestParam("tipoIncidencia") String tipoIncidencia,
            @RequestParam("categoria") String categoria,
            @RequestParam("prioridad") String prioridad,
            @RequestParam("ubicacion") String ubicacion,
            HttpSession session) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        // Creamos y llenamos la incidencia
        Incidencia nueva = new Incidencia();
        nueva.setTipoIncidencia(tipoIncidencia);
        nueva.setCategoria(categoria);
        nueva.setPrioridad(prioridad);
        nueva.setUbicacion(ubicacion);
        nueva.setUsuario(usuarioLogueado);
        
        // ESTA ES LA LÍNEA MÁGICA QUE FALTABA
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

        // Asumiendo que en tu IncidenciaService tienes un método para obtener TODAS
        // Si no se llama "obtenerTodas()", cámbialo por el nombre que tu compañero le haya puesto (ej. findAll())
        List<Incidencia> todasIncidencias = incidenciaService.obtenerTodas();

        long pendientes = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("PENDIENTE")).count();
        long enProceso = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("EN PROCESO")).count();
        long resueltas = todasIncidencias.stream().filter(i -> i.getEstado().equalsIgnoreCase("RESUELTA")).count();

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("incidencias", todasIncidencias);
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalEnProceso", enProceso);
        model.addAttribute("totalResueltas", resueltas);

        return "panel-tecnico"; // Buscará panel-tecnico.html
    }

    // Método para que el técnico cambie el estado de la incidencia
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
                incidencia.setFechaResolucion(java.time.LocalDateTime.now());
            }  
            
            // LÓGICA DE SUBIDA DE EVIDENCIA
            // Si el estado es RESUELTA y el técnico subió un archivo que no está vacío
            if (nuevoEstado.equals("RESUELTA") && archivo != null && !archivo.isEmpty()) {
                try {
                    // 1. Usamos Apache Commons IO para extraer la extensión segura (ej. jpg, png)
                    String extension = FilenameUtils.getExtension(archivo.getOriginalFilename());
                    
                    // 2. Creamos un nombre único para evitar que fotos con el mismo nombre se chanquen
                    String nombreFoto = "evidencia_ticket_" + idIncidencia + "." + extension;
                    
                    // 3. Definimos la carpeta física donde se guardará (se creará en la raíz del proyecto SGI)
                    Path rutaCarpeta = Paths.get("uploads/evidencias");
                    if (!Files.exists(rutaCarpeta)) {
                        Files.createDirectories(rutaCarpeta);
                    }
                    
                    // 4. Copiamos el archivo del navegador a nuestra carpeta física
                    Path rutaCompleta = rutaCarpeta.resolve(nombreFoto);
                    Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
                    
                    // 5. Guardamos solo el nombre del archivo en MySQL
                    incidencia.setEvidenciaUrl(nombreFoto);
                    
                } catch (IOException | IllegalArgumentException e) {
                    System.out.println("Error fatal al subir la foto de evidencia: " + e.getMessage());
                }
            }

            // Guardamos todo (el nuevo estado, fecha y nombre de la foto) en la base de datos
            incidenciaService.guardar(incidencia);
        }
        
        return "redirect:/incidencias/panel-tecnico";
    }
}
