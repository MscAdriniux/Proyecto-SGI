package com.sgi.controller;
import jakarta.servlet.http.HttpSession;
import com.sgi.model.Usuario;
import com.sgi.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditoriaController {

    @Autowired
    private AuditService auditService;

    @GetMapping("/admin/auditoria")
    public String mostrarAuditoria(HttpSession session, Model model) {

        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");

        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        return "redirect:/admin/panel-admin?seccion=auditoria";
    }

}