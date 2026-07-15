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

        // 1. Lógica de seguridad (proveniente de main)
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");

        if (u == null || !u.getRol().equalsIgnoreCase("administrador")) {
            return "redirect:/login";
        }

        // 2. Lógica funcional (proveniente de tu rama Adrian_V2)
        var logs = auditService.obtenerLogs();

        long info = logs.stream()
                .filter(log -> "INFO".equalsIgnoreCase(log.getNivel()))
                .count();

        long warn = logs.stream()
                .filter(log -> "WARN".equalsIgnoreCase(log.getNivel()))
                .count();

        long error = logs.stream()
                .filter(log -> "ERROR".equalsIgnoreCase(log.getNivel()))
                .count();

        model.addAttribute("logs", logs);
        model.addAttribute("info", info);
        model.addAttribute("warn", warn);
        model.addAttribute("error", error);

        return "auditoria";
    }
}