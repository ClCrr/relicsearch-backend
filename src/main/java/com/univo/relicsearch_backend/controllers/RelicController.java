package com.univo.relicsearch_backend.controllers;

import com.univo.relicsearch_backend.dto.RelicRequest;
import com.univo.relicsearch_backend.dto.RelicResponse;
import com.univo.relicsearch_backend.models.Relic;
import com.univo.relicsearch_backend.repositories.UsuarioRepository;
import com.univo.relicsearch_backend.services.RelicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relics")
public class RelicController {

    private final RelicService relicService;
    private final UsuarioRepository usuarioRepository;

    public RelicController(RelicService relicService, UsuarioRepository usuarioRepository) {
        this.relicService = relicService;
        this.usuarioRepository = usuarioRepository;
    }

    // Método auxiliar para obtener el ID del usuario dueño del Token
    private Long obtenerUsuarioIdAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    @GetMapping
    public ResponseEntity<List<RelicResponse>> obtenerMisReliquias() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        List<Relic> relics = relicService.obtenerReliquiasPorUsuario(usuarioId);

        // Convertimos la lista de entidades a lista de DTOs
        List<RelicResponse> response = relics.stream()
                .map(RelicResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RelicResponse> crearReliquia(@RequestBody RelicRequest request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Relic nuevaReliquia = relicService.crearReliquia(usuarioId, request.getNombre());
        return ResponseEntity.ok(new RelicResponse(nuevaReliquia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelicResponse> obtenerReliquia(@PathVariable Long id) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Relic relic = relicService.obtenerPorId(id, usuarioId);
        return ResponseEntity.ok(new RelicResponse(relic));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReliquia(@PathVariable Long id) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        relicService.eliminarReliquia(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}