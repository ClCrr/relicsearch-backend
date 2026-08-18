package com.univo.relicsearch_backend.controllers;

import com.univo.relicsearch_backend.dto.BuscarRelicResponse;
import com.univo.relicsearch_backend.models.BuscarRelic;
import com.univo.relicsearch_backend.repositories.UsuarioRepository;
import com.univo.relicsearch_backend.services.BuscarRelicService;
import com.univo.relicsearch_backend.services.GroqService; // <-- Importamos Groq
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BuscarRelicController {

    private final BuscarRelicService buscarRelicService;
    private final UsuarioRepository usuarioRepository;
    private final GroqService groqService; // <-- Declaramos GroqService


    // Inyectamos los 3 servicios en el constructor
    public BuscarRelicController(BuscarRelicService buscarRelicService,
                                 UsuarioRepository usuarioRepository,
                                 GroqService groqService) {
        this.buscarRelicService = buscarRelicService;
        this.usuarioRepository = usuarioRepository;
        this.groqService = groqService;
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

    // Extraer información de MediaWiki y guardarla
    @PostMapping("/relics/{relicId}/contenidos/extract")
    public ResponseEntity<BuscarRelicResponse> extraerContenido(@PathVariable Long relicId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        BuscarRelic nuevaExtraccion = buscarRelicService.extraerYGuardarContenido(relicId, usuarioId);
        return ResponseEntity.ok(new BuscarRelicResponse(nuevaExtraccion));
    }

    // Eliminar un contenido específico
    @DeleteMapping("/contenidos/{id}")
    public ResponseEntity<Void> eliminarContenido(@PathVariable Long id) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        buscarRelicService.eliminarExtraccion(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // --- NUEVO ENDPOINT PARA LA INTELIGENCIA ARTIFICIAL CON GROQ ---
    @PostMapping("/contenidos/{id}/analizar")
    public ResponseEntity<Map<String, String>> analizarExtraccion(@PathVariable Long id) {
        Long usuarioId = obtenerUsuarioIdAutenticado();

        // 1. Buscamos el contenido en la base de datos
        BuscarRelic extraccion = buscarRelicService.obtenerPorId(id, usuarioId);

        // 2. Se lo enviamos a Groq (Llama 3)
        String analisisIA = groqService.analizarContenido(extraccion.getContenido());

        // 3. Respondemos con un JSON amigable
        Map<String, String> response = new HashMap<>();
        response.put("titulo", extraccion.getTitulo());
        response.put("analisisIA", analisisIA);

        return ResponseEntity.ok(response);
    }
}