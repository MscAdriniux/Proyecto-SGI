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

/**
 * Controlador encargado de gestionar el acceso al sistema.
 * Maneja el inicio de sesión (login), el registro de nuevos usuarios, 
 * el enrutamiento inteligente según el rol y el cierre de sesión seguro.
 */
@Controller
public class LoginController {

    /**
     * Constructor por defecto.
     */
    public LoginController() {}
    
    @Autowired
    private UsuarioService usuarioService;

    // ==========================================
    // 1. RUTAS DE INICIO Y LOGIN
    // ==========================================

    /**
     * Intercepta la ruta raíz del sistema y redirige al usuario a la pantalla de autenticación.
     * @return Redirección a la ruta /login.
     */
    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    /**
     * Muestra la vista del formulario de inicio de sesión.
     * @return El nombre de la plantilla HTML de login.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Busca el archivo login.html
    }

    /**
     * Procesa las credenciales, autentica al usuario y lo enruta al panel correspondiente a su rol.
     * @param correo El correo electrónico ingresado en el formulario.
     * @param contrasena La contraseña ingresada en el formulario.
     * @param session La sesión HTTP para almacenar de forma persistente los datos del usuario.
     * @param model El modelo para enviar mensajes de error a la vista en caso de fallo.
     * @return Redirección al panel (Docente, Soporte TI o Admin) o recarga el login con error.
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo, 
                                @RequestParam("contrasena") String contrasena, 
                                HttpSession session, 
                                Model model) {
                                    
        Usuario usuarioAutenticado = usuarioService.autenticarUsuario(correo, contrasena);

        // PRIMERA VALIDACIÓN: ¿Existe el usuario y la contraseña es correcta?
        if (usuarioAutenticado != null) {
            
            // Guardamos al usuario en la sesión para que las demás pantallas lo reconozcan
            session.setAttribute("usuarioLogueado", usuarioAutenticado); 
            // Obtenemos el rol
            String rol = usuarioAutenticado.getRol(); 
            // Salvavidas por si un usuario viejo no tiene rol en la BD
            if (rol == null) {
                rol = "docente"; 
            }  
            // Limpiamos espacios accidentales en la base de datos
            rol = rol.trim(); 
            
            // ENRUTADOR INTELIGENTE
            if (rol.equalsIgnoreCase("administrador") || rol.equalsIgnoreCase("admin")) {
                return "redirect:/admin/dashboard";
                
            } else if (rol.equalsIgnoreCase("soporte ti") || rol.equalsIgnoreCase("tecnico")) {
                return "redirect:/incidencias/panel-tecnico";
                
            } else {
                return "redirect:/incidencias/mis-incidencias";
            }
            
        } else {
            // Si llega aquí, significa que se equivocó de correo o contraseña
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login"; // Lo devuelve a la vista login.html con el mensaje de error
        }
    }
    
    // ==========================================
    // 2. RUTAS DE REGISTRO
    // ==========================================

    /**
     * Muestra la vista con el formulario para registrar un nuevo usuario en el sistema.
     * @return El nombre de la plantilla HTML de registro.
     */
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro"; // Busca el archivo registro.html
    }

    /**
     * Procesa los datos de registro, valida que el correo no esté duplicado y persiste el usuario.
     * @param nombres Los nombres del nuevo usuario.
     * @param apellidos Los apellidos del nuevo usuario.
     * @param correo El correo electrónico (debe ser único en el sistema).
     * @param contrasena La contraseña en texto plano (será encriptada por el servicio).
     * @param rol El rol asignado (docente, tecnico, administrador).
     * @param model El modelo para notificar a la vista si el correo ya existe.
     * @return Redirección al login en caso de éxito, o recarga el registro mostrando el error.
     */
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

    /**
     * Destruye la sesión activa del usuario actual para garantizar un cierre de sesión seguro.
     * @param session La sesión HTTP actual que será invalidada.
     * @return Redirección a la pantalla de login.
     */
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ==========================================
    // 4. RUTAS TEMPORALES DE PRUEBA (Para evitar el 404)
    // ==========================================

    /**
     * Endpoint temporal (Placeholder) para verificar el enrutamiento del rol Administrador.
     * Evita errores 404 en el sistema hasta que se desarrolle la vista real del dashboard.
     * @param session La sesión HTTP actual para recuperar y validar los datos del usuario.
     * @return Código HTML inyectado directamente en el navegador con confirmación de éxito o error.
     */
    @GetMapping("/admin/dashboard")
    @ResponseBody 
    public String panelAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if(u == null) return "Acceso denegado. <a href='/login'>Inicia sesión</a>";
        return "<h1>¡Éxito total!</h1><p>Bienvenido Administrador <b>" + u.getNombres() + "</b>. Tu login funciona perfecto.</p><br><a href='/logout'>Cerrar Sesión</a>";
    }
}