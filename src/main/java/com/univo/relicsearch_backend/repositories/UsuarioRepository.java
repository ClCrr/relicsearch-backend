package com.univo.relicsearch_backend.repositories;

import com.univo.relicsearch_backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Boot entenderá este nombre y creará la query SQL automáticamente
    Optional<Usuario> findByEmail(String email);
}