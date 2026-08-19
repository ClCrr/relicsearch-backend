package com.univo.relicsearch_backend.security; // Ajusta el paquete según tu estructura

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a todas las rutas de tu API (ej. /api/auth/login)
                        .allowedOrigins(
                                "http://localhost:4200", // Permite tu Angular en desarrollo
                                "https://tu-frontend.vercel.app" // [OPCIONAL] Permite tu Angular en producción (cámbialo luego)
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // El método OPTIONS es vital para evitar el error CORS
                        .allowedHeaders("*") // Permite cualquier cabecera (incluyendo Authorization para el JWT)
                        .allowCredentials(true); // Necesario si envías cookies o tokens
            }
        };
    }
}