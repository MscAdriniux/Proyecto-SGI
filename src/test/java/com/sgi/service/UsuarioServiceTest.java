package com.sgi.service;

import com.sgi.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    // PRUEBA 3: AUTENTICACIÓN CORRECTA
    @Test
    public void testAutenticarUsuarioCorrecto() {
        Usuario u = usuarioService.autenticarUsuario("jperez@utp.edu.pe", "12345");
        
        assertNotNull(u, "El resultado debería ser un objeto Usuario (no nulo)");
    }

    // PRUEBA 4: CONTRASEÑA INCORRECTA
    @Test
    public void testAutenticarContrasenaIncorrecta() {
        Usuario u = usuarioService.autenticarUsuario("jperez@utp.edu.pe", "123456");
        
        assertNull(u, "El resultado debería ser null porque la contraseña está mal");
    }
}