package com.sgi.service;

import com.sgi.model.Usuario;
import com.sgi.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void testAutenticarUsuarioCorrecto() {
        // Arrange: preparar datos
        String email = "jperez@utp.edu.pe";
        String passPlana = "12345";
        
        Usuario mockUser = new Usuario();
        mockUser.setCorreo(email);
        mockUser.setContrasena(encoder.encode(passPlana));

        // Arrange: configurar mock
        when(usuarioRepository.findByCorreo(email)).thenReturn(mockUser);

        // Act: ejecutar método
        Usuario resultado = usuarioService.autenticarUsuario(email, passPlana);

        // Assert: verificar resultados
        assertNotNull(resultado);
        assertEquals(email, resultado.getCorreo());
    }

    @Test
    public void testAutenticarContrasenaIncorrecta() {
        // Arrange: preparar datos
        String email = "jperez@utp.edu.pe";
        String passCorrecta = "12345";
        String passMal = "99999";
        
        Usuario mockUser = new Usuario();
        mockUser.setCorreo(email);
        mockUser.setContrasena(encoder.encode(passCorrecta));

        // Arrange: configurar mock
        when(usuarioRepository.findByCorreo(email)).thenReturn(mockUser);

        // Act: ejecutar método
        Usuario resultado = usuarioService.autenticarUsuario(email, passMal);

        // Assert: verificar null
        assertNull(resultado);
    }
}