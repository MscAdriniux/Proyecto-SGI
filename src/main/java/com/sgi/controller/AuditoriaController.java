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

        if (u == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogueado", u);

        model.addAttribute("logs", auditService.obtenerLogs());

        model.addAttribute("info", auditService.contarInfo());

        model.addAttribute("warn", auditService.contarWarn());

        model.addAttribute("error", auditService.contarError());

        return "auditoria";
    }

}