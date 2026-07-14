package com.sgi.service.impl;

import com.sgi.service.UsuarioService;
import com.sgi.model.Usuario;
import com.sgi.repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar la lógica de negocio de los usuarios.
 * Maneja operaciones críticas de seguridad como la autenticación (login), 
 * la validación de duplicados y la encriptación de contraseñas para el registro.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    /**
     * Constructor por defecto.
     */
    public UsuarioServiceImpl() {}
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Creamos la herramienta que encripta y compara
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Autentica a un usuario verificando su existencia y comprobando de forma segura su contraseña.
     * Utiliza BCrypt para comparar el texto plano introducido con el hash guardado en la base de datos.
     * @param correo El correo electrónico ingresado en el login.
     * @param contrasenaPlana La contraseña tal cual fue escrita por el usuario.
     * @return El objeto Usuario si las credenciales son correctas, o null si el login falla.
     */
    @Override
    public Usuario autenticarUsuario(String correo, String contrasenaPlana) {
        // 1. Buscamos al usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        
        // 2. Si el usuario existe, comparamos la contraseña plana con el hash de la BD
        if (usuario != null && passwordEncoder.matches(contrasenaPlana, usuario.getContrasena())) {
            return usuario; // Login exitoso
        }
        
        return null; // Login fallido
    }
    
    /**
     * Verifica si una dirección de correo ya se encuentra registrada en la base de datos.
     * Utilizado para prevenir registros duplicados y colisiones en el inicio de sesión.
     * @param correo El correo electrónico a verificar.
     * @return true si el correo ya existe, false si está disponible.
     */
    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo) != null;
    }

    /**
     * Registra de forma segura un nuevo usuario en el sistema.
     * Intercepta la contraseña en texto plano, la encripta usando BCrypt y luego guarda la entidad.
     * @param nuevoUsuario El objeto Usuario con los datos del formulario de registro.
     * @return El usuario ya persistido en la base de datos con su ID generado y contraseña encriptada.
     */
    @Override
    public Usuario registrarUsuario(Usuario nuevoUsuario) {
        // Encriptamos la clave
        String hashPassword = passwordEncoder.encode(nuevoUsuario.getContrasena());
        nuevoUsuario.setContrasena(hashPassword);
        
        // Guardamos el usuario ya seguro en la BD
        return usuarioRepository.save(nuevoUsuario);
    }
    
    /**
     * Obtiene una lista de usuarios filtrados por su rol en el sistema.
     * @param rol El rol a buscar (ej. "docente", "administrador").
     * @return Una lista de usuarios que poseen el rol especificado.
     */
    @Override
    public List<Usuario> obtenerPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }
}