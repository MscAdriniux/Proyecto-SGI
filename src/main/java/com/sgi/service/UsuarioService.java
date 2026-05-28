package com.sgi.service;

import com.sgi.model.Usuario;
import com.sgi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Creamos la herramienta que encripta y compara
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario autenticarUsuario(String correo, String contrasenaPlana) {
        // 1. Buscamos al usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        
        // 2. Si el usuario existe, comparamos la contraseña plana con el hash de la BD
        if (usuario != null && passwordEncoder.matches(contrasenaPlana, usuario.getContrasena())) {
            return usuario; // Login exitoso
        }
        
        return null; // Login fallido
    }
    
    // Método para comprobar si el correo ya existe antes de registrarlo
    public boolean existeCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo) != null;
    }

    // Método para registrar encriptando la contraseña
    public Usuario registrarUsuario(Usuario nuevoUsuario) {
        // Encriptamos la clave
        String hashPassword = passwordEncoder.encode(nuevoUsuario.getContrasena());
        nuevoUsuario.setContrasena(hashPassword);
        
        // Guardamos el usuario ya seguro en la BD
        return usuarioRepository.save(nuevoUsuario);
    }
    
    
}