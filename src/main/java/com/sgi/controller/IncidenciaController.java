package com.sgi.controller;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.service.IncidenciaService;
import jakarta.servlet.http.HttpSession;
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
}