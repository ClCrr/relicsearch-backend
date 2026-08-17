package com.univo.relicsearch_backend.repositories;

import com.univo.relicsearch_backend.models.Relic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RelicRepository extends JpaRepository<Relic, Long> {
    // Para asegurarnos de que un usuario solo vea sus propias reliquias
    List<Relic> findByUsuarioId(Long usuarioId);
}