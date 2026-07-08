package com.sgi.controller;

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
    public String mostrarAuditoria(Model model) {

        model.addAttribute("logs", auditService.obtenerLogs());

        return "auditoria";
    }

}