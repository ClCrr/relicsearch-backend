package com.univo.relicsearch_backend.services;

import com.univo.relicsearch_backend.models.BuscarRelic;
import com.univo.relicsearch_backend.models.Relic;
import com.univo.relicsearch_backend.repositories.BuscarRelicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BuscarRelicService {

    private final BuscarRelicRepository buscarRelicRepository;
    private final RelicService relicService;
    private final MediaWikiService mediaWikiService; // NUEVA INYECCIÓN

    public BuscarRelicService(BuscarRelicRepository buscarRelicRepository,
                              RelicService relicService,
                              MediaWikiService mediaWikiService) {
        this.buscarRelicRepository = buscarRelicRepository;
        this.relicService = relicService;
        this.mediaWikiService = mediaWikiService;
    }

    // --- MÉTODOS NUEVOS ---

    public BuscarRelic extraerYGuardarContenido(Long relicId, Long usuarioId) {
        // 1. Validamos que la reliquia exista y pertenezca a este usuario
        Relic relic = relicService.obtenerPorId(relicId, usuarioId);

        // 2. Extraemos el contenido de MediaWiki usando el nombre de la reliquia (Ej: "Lith A12")
        String[] datosWiki = mediaWikiService.obtenerContenidoReliquia(relic.getNombre());
        String titulo = datosWiki[0];
        String contenido = datosWiki[1];

        // 3. Creamos el registro y lo guardamos
        BuscarRelic extraccion = new BuscarRelic();
        extraccion.setTitulo(titulo);
        extraccion.setContenido(contenido);
        extraccion.setUrl("https://wiki.warframe.com/wiki/" + titulo.replace(" ", "_"));
        extraccion.setRelic(relic);

        return buscarRelicRepository.save(extraccion);
    }

    // Este método lo necesitaremos para la Fase de IA
    public BuscarRelic obtenerPorId(Long extraccionId, Long usuarioId) {
        BuscarRelic extraccion = buscarRelicRepository.findById(extraccionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Extracción no encontrada"));

        if (!extraccion.getRelic().getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta extracción");
        }
        return extraccion;
    }

    // --- MÉTODOS ANTERIORES (Mantenerlos) ---

    public List<BuscarRelic> obtenerExtraccionesDeReliquia(Long relicId, Long usuarioId) {
        Relic relic = relicService.obtenerPorId(relicId, usuarioId);
        return buscarRelicRepository.findByRelicId(relic.getId());
    }

    public void eliminarExtraccion(Long extraccionId, Long usuarioId) {
        BuscarRelic extraccion = obtenerPorId(extraccionId, usuarioId);
        buscarRelicRepository.delete(extraccion);
    }
}