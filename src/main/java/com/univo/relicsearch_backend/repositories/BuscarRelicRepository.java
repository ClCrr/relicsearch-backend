package com.univo.relicsearch_backend.repositories;

import com.univo.relicsearch_backend.models.BuscarRelic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BuscarRelicRepository extends JpaRepository<BuscarRelic, Long> {
    // Para listar el historial de extracciones de una reliquia en particular
    List<BuscarRelic> findByRelicId(Long relicId);
}