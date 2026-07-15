package com.sgi.controller;

import com.sgi.model.Usuario;
import com.sgi.service.CatalogoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de la vista de CRUD de Catálogo (página independiente,
 * al mismo nivel que el Centro de Métricas y la Auditoría).
 * El CRUD en sí (crear/editar/eliminar) sigue viviendo en CatalogoRestController,
 * este controlador solo entrega la pantalla y los datos iniciales de la tabla.
 */
@Controller
public class CatalogoViewController {

    @Autowired
    private CatalogoService catalogoService;

    @GetMapping("/admin/catalogo")
    public String verCrudCatalogo(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogueado", u);
        model.addAttribute("catalogo", catalogoService.obtenerTodoElCatalogo());

        return "centro-catalogo";
    }
}