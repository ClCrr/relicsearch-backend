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

    public BuscarRelicService(BuscarRelicRepository buscarRelicRepository, RelicService relicService) {
        this.buscarRelicRepository = buscarRelicRepository;
        this.relicService = relicService;
    }

    public List<BuscarRelic> obtenerExtraccionesDeReliquia(Long relicId, Long usuarioId) {
        // Al llamar a obtenerPorId de RelicService, automáticamente validamos si el usuario es dueño de la reliquia
        Relic relic = relicService.obtenerPorId(relicId, usuarioId);
        return buscarRelicRepository.findByRelicId(relic.getId());
    }

    public void eliminarExtraccion(Long extraccionId, Long usuarioId) {
        BuscarRelic extraccion = buscarRelicRepository.findById(extraccionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Extracción no encontrada"));

        // Validamos la propiedad a través de la relación: BuscarRelic -> Relic -> Usuario
        if (!extraccion.getRelic().getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta extracción");
        }

        buscarRelicRepository.delete(extraccion);
    }
}