/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sgi.service;

import com.sgi.model.Usuario;
import java.util.List;

/**
 *
 * @author ad_ri
 */
public interface UsuarioService {

    /**
     * Autentica a un usuario verificando su existencia y comprobando de forma segura su contraseña.
     * Utiliza BCrypt para comparar el texto plano introducido con el hash guardado en la base de datos.
     * @param correo El correo electrónico ingresado en el login.
     * @param contrasenaPlana La contraseña tal cual fue escrita por el usuario.
     * @return El objeto Usuario si las credenciales son correctas, o null si el login falla.
     */
    Usuario autenticarUsuario(String correo, String contrasenaPlana);

    /**
     * Verifica si una dirección de correo ya se encuentra registrada en la base de datos.
     * Utilizado para prevenir registros duplicados y colisiones en el inicio de sesión.
     * @param correo El correo electrónico a verificar.
     * @return true si el correo ya existe, false si está disponible.
     */
    boolean existeCorreo(String correo);

    /**
     * Obtiene una lista de usuarios filtrados por su rol en el sistema.
     * @param rol El rol a buscar (ej. "docente", "administrador").
     * @return Una lista de usuarios que poseen el rol especificado.
     */
    List<Usuario> obtenerPorRol(String rol);

    /**
     * Registra de forma segura un nuevo usuario en el sistema.
     * Intercepta la contraseña en texto plano, la encripta usando BCrypt y luego guarda la entidad.
     * @param nuevoUsuario El objeto Usuario con los datos del formulario de registro.
     * @return El usuario ya persistido en la base de datos con su ID generado y contraseña encriptada.
     */
    Usuario registrarUsuario(Usuario nuevoUsuario);
    
}
