package com.univo.relicsearch_backend.controllers;

import com.univo.relicsearch_backend.dto.AuthResponse;
import com.univo.relicsearch_backend.dto.LoginRequest;
import com.univo.relicsearch_backend.dto.RegisterRequest;
import com.univo.relicsearch_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException; // <-- Importación actualizada
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("---- 1. LLEGÓ LA PETICIÓN AL CONTROLADOR ----");
        System.out.println("Email recibido: " + request.getEmail());

        try {
            System.out.println("---- 2. ENTRANDO AL AUTH SERVICE ----");
            AuthResponse response = authService.login(request);

            System.out.println("---- 3. LOGIN EXITOSO ----");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("---- 4. ERROR ATRAPADO EN CONTROLADOR: " + e.getMessage() + " ----");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Usuario o contraseña incorrectos"));
        }
    }
}