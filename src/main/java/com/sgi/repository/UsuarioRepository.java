package com.sgi.repository;

import com.sgi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Spring Data JPA para la entidad Usuario.
 * Maneja las operaciones de base de datos relacionadas con los usuarios del sistema.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca un usuario exacto utilizando su dirección de correo electrónico.
     * Es la pieza clave para el sistema de inicio de sesión (Login) y para 
     * validar que no se registren correos duplicados.
     * @param correo El correo electrónico introducido por el usuario.
     * @return La entidad Usuario si se encuentra coincidencia, o null si no existe.
     */
    Usuario findByCorreo(String correo);
}