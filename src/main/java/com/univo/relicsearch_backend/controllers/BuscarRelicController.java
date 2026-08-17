package com.univo.relicsearch_backend.controllers;

import com.univo.relicsearch_backend.dto.BuscarRelicResponse;
import com.univo.relicsearch_backend.models.BuscarRelic;
import com.univo.relicsearch_backend.repositories.UsuarioRepository;
import com.univo.relicsearch_backend.services.BuscarRelicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BuscarRelicController {

    private final BuscarRelicService buscarRelicService;
    private final UsuarioRepository usuarioRepository;

    public BuscarRelicController(BuscarRelicService buscarRelicService, UsuarioRepository usuarioRepository) {
        this.buscarRelicService = buscarRelicService;
        this.usuarioRepository = usuarioRepository;
    }

    private Long obtenerUsuarioIdAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email).orElseThrow().getId();
    }

    // Obtener los contenidos extraídos de una reliquia específica
    @GetMapping("/relics/{relicId}/contenidos")
    public ResponseEntity<List<BuscarRelicResponse>> obtenerContenidosDeReliquia(@PathVariable Long relicId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        List<BuscarRelic> contenidos = buscarRelicService.obtenerExtraccionesDeReliquia(relicId, usuarioId);

        List<BuscarRelicResponse> response = contenidos.stream()
                .map(BuscarRelicResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Eliminar un contenido específico
    @DeleteMapping("/contenidos/{id}")
    public ResponseEntity<Void> eliminarContenido(@PathVariable Long id) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        buscarRelicService.eliminarExtraccion(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}