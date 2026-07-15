package com.sgi.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger =
            LoggerFactory.getLogger(ReporteController.class);

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
 
    logger.info(
        "AUDITORIA | Módulo=Centro de Reportes | Usuario={} | Acción=Ingresó al Centro de Reportes",
        u.getCorreo()
    );

   
    return "redirect:/admin/panel-admin?seccion=reportes";
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
        
        System.out.println("Cantidad de incidencias: " + listaAExportar.size());

        // Si no hay incidencias
        if (listaAExportar.isEmpty()) {

            logger.warn(
                "AUDITORIA | Módulo=Centro de Reportes | Usuario={} | Acción=Intentó exportar un reporte sin incidencias",
                u.getCorreo()
            );

            return ResponseEntity.noContent().build();
        }

        // Sí se exportó
        logger.info(
            "AUDITORIA | Módulo=Centro de Reportes | Usuario={} | Acción=Exportó reporte Excel",
            u.getCorreo()
        );

        byte[] excelBytes = reporteService.generarReporte(listaAExportar);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Reporte_Incidencias_SGI.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}   