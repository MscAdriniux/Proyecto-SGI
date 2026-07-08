package com.sgi.controller;

import com.sgi.model.Incidencia;
import com.sgi.model.Usuario;
import com.sgi.service.IncidenciaService;
import com.sgi.service.ReporteService; 
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReporteController {

    @Autowired
    private IncidenciaService incidenciaService;

 
    @Autowired
    private ReporteService reporteService;

    @GetMapping("/admin/herramientas/reportes")
    public String verCentroReportes(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogueado", u);
        model.addAttribute("incidencias", incidenciaService.obtenerTodas());
        return "centro-reportes";
    }

    @GetMapping("/admin/reporte/excel-selectivo")
    public ResponseEntity<byte[]> descargarExcelSelectivo(
            @RequestParam(value = "ids", required = false) List<Integer> ids,
            HttpSession session) throws IOException {

        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return ResponseEntity.status(403).build();
        }

        List<Incidencia> listaAExportar;
        if (ids != null && !ids.isEmpty()) {
            listaAExportar = incidenciaService.obtenerPorListaIds(ids);
        } else {
            listaAExportar = incidenciaService.obtenerTodas();
        }

        // Llamamos a la interfaz de reporte (cumpliendo OCP)
        byte[] excelBytes = reporteService.generarReporte(listaAExportar);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Incidencias_SGI.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}