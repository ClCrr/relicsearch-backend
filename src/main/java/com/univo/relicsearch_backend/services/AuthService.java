package com.univo.relicsearch_backend.services;

import com.univo.relicsearch_backend.dto.AuthResponse;
import com.univo.relicsearch_backend.dto.LoginRequest;
import com.univo.relicsearch_backend.dto.RegisterRequest;
import com.univo.relicsearch_backend.models.Usuario;
import com.univo.relicsearch_backend.repositories.UsuarioRepository;
import com.univo.relicsearch_backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // ¡Encriptada!

        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        // Esto verifica automáticamente la contraseña con BCrypt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtService.generarToken(request.getEmail());
        return new AuthResponse(token);
    }
}