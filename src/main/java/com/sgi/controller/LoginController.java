package com.sgi.controller;

import com.sgi.model.Usuario;
import com.sgi.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    // ==========================================
    // 1. RUTAS DE INICIO Y LOGIN
    // ==========================================

    // Si entras a la raíz (localhost:8080/), te manda al login
    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    // Mostrar la página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Busca el archivo login.html
    }

    // Procesar el formulario de login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo, 
                                @RequestParam("contrasena") String contrasena, 
                                HttpSession session, 
                                Model model) {
                                    
        Usuario usuarioAutenticado = usuarioService.autenticarUsuario(correo, contrasena);

        if (usuarioAutenticado != null) {
            // Guardamos al usuario en la sesión
            session.setAttribute("usuarioLogueado", usuarioAutenticado);
            
            // Redirigimos según su rol
            String rol = usuarioAutenticado.getRol().toLowerCase();
            if (rol.equals("administrador")) {
                return "redirect:/admin/dashboard";
            } else if (rol.equals("soporte ti") || rol.equals("ti")) {
                return "redirect:/ti/dashboard";
            } else {
                return "redirect:/incidencias/mis-incidencias";
            }
        } else {
            // Error en credenciales
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }

    // ==========================================
    // 2. RUTAS DE REGISTRO
    // ==========================================

    // Mostrar la página de registro
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro"; // Busca el archivo registro.html
    }

    // Procesar el formulario de registro
    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam("nombres") String nombres,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            @RequestParam("rol") String rol,
            Model model) {
        
        // Validar que el correo no esté duplicado
        if (usuarioService.existeCorreo(correo)) {
            model.addAttribute("error", "Ese correo ya está registrado.");
            return "registro";
        }
        
        // Armar el nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombres(nombres);
        nuevoUsuario.setApellidos(apellidos);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setRol(rol);
        
        // Guardar el usuario encriptado
        usuarioService.registrarUsuario(nuevoUsuario);
        
        return "redirect:/login";
    }
    
    // ==========================================
    // 3. RUTA DE LOGOUT (CERRAR SESIÓN)
    // ==========================================

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}