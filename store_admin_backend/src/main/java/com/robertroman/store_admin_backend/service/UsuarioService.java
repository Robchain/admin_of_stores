package com.robertroman.store_admin_backend.service;

import com.robertroman.store_admin_backend.dto.AuthResponse;
import com.robertroman.store_admin_backend.dto.LoginRequest;
import com.robertroman.store_admin_backend.dto.RegisterRequest;
import com.robertroman.store_admin_backend.entity.Usuario;
import com.robertroman.store_admin_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Registrar nuevo usuario
    public AuthResponse registrarUsuario(RegisterRequest request) {
        // Verificar si el username ya existe
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Guardar en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Generar token JWT
        String token = jwtService.generateToken(usuarioGuardado.getUsername());

        // Retornar respuesta
        return new AuthResponse(token, usuarioGuardado.getUsername(), usuarioGuardado.getEmail());
    }

    // Login de usuario
    public AuthResponse loginUsuario(LoginRequest request) {
        // Buscar usuario por username o email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(),
                request.getUsernameOrEmail()
        );

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtService.generateToken(usuario.getUsername());

        // Retornar respuesta
        return new AuthResponse(token, usuario.getUsername(), usuario.getEmail());
    }

    // Buscar usuario por username
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Verificar si existe username
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // Verificar si existe email
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}