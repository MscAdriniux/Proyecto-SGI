package com.sgi.repository;

import com.sgi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Spring Boot crea automáticamente la consulta SQL con solo nombrar bien el método
    Usuario findByCorreo(String correo);
}