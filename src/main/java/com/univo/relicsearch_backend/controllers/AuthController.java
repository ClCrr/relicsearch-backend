package com.univo.relicsearch_backend.controllers;

import com.univo.relicsearch_backend.dto.AuthResponse;
import com.univo.relicsearch_backend.dto.LoginRequest;
import com.univo.relicsearch_backend.dto.RegisterRequest;
import com.univo.relicsearch_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    // 1. Cambiamos el tipo de retorno a ResponseEntity<?> para poder devolver tanto el AuthResponse como un Map de error.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 2. Ejecutamos la lógica de tu servicio
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // 3. Atrapamos explícitamente las credenciales inválidas y devolvemos un 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Usuario o contraseña incorrectos"));

        } catch (Exception e) {
            // 4. (Opcional) Capturamos cualquier otro error inesperado para evitar que Spring devuelva HTML
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error interno al procesar la solicitud"));
        }
    }
}