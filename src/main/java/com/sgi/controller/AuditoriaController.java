package com.sgi.controller;

import com.sgi.service.AuditService; // <-- CAMBIO 1: Importamos la INTERFAZ, no el impl
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditoriaController {

    @Autowired
    private AuditService auditService; // <-- CAMBIO 2: Inyectamos la INTERFAZ

    @GetMapping("/admin/auditoria")
    public String mostrarAuditoria(Model model) {

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