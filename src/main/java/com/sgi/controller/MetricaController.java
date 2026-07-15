package com.sgi.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sgi.model.Usuario;
import com.sgi.service.MetricaService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MetricaController {
    private static final Logger logger =
        LoggerFactory.getLogger(MetricaController.class);

    @Autowired
    private MetricaService metricaService;

    // Ruta principal para cargar el panel de métricas
    @GetMapping("/admin/herramientas/metricas")
    public String verCentroMetricas(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }
        logger.info(
            "AUDITORIA | Módulo=Centro de Métricas | Usuario={} | Acción=Ingresó al Centro de Métricas",
            u.getCorreo()
        );

        // Cargamos los datos iniciales para las tarjetas y listas fijas
        model.addAttribute("usuarioLogueado", u);
        model.addAttribute("kpis", metricaService.obtenerKpisDiarios());
        model.addAttribute("topUbicaciones", metricaService.obtenerTopUbicaciones());
        model.addAttribute("categorias", metricaService.obtenerDistribucionCategorias());
        model.addAttribute("aulasBloqueadas", metricaService.obtenerAulasBloqueadas());

        return "centro-metricas";
    }

    // Endpoint REST asíncrono para el toggle del gráfico de técnicos (Retorna JSON)
    @GetMapping("/api/metricas/tecnicos")
    @ResponseBody
    public Map<String, Long> obtenerRendimientoTecnicosAPI(@RequestParam(value = "periodo", defaultValue = "hoy") String periodo) {
        return metricaService.obtenerRendimientoTecnicos(periodo);
    }
}