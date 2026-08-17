package com.univo.relicsearch_backend.services;

import com.univo.relicsearch_backend.models.Relic;
import com.univo.relicsearch_backend.models.Usuario;
import com.univo.relicsearch_backend.repositories.RelicRepository;
import com.univo.relicsearch_backend.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RelicService {

    private final RelicRepository relicRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias mediante constructor (Mejor práctica en Spring)
    public RelicService(RelicRepository relicRepository, UsuarioRepository usuarioRepository) {
        this.relicRepository = relicRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Relic> obtenerReliquiasPorUsuario(Long usuarioId) {
        return relicRepository.findByUsuarioId(usuarioId);
    }

    public Relic crearReliquia(Long usuarioId, String nombre) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Relic relic = new Relic();
        relic.setNombre(nombre);
        relic.setUsuario(usuario);

        return relicRepository.save(relic);
    }

    public Relic obtenerPorId(Long relicId, Long usuarioId) {
        Relic relic = relicRepository.findById(relicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reliquia no encontrada"));

        // VERIFICACIÓN DE SEGURIDAD: ¿La reliquia pertenece a este usuario?
        if (!relic.getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta reliquia");
        }

        return relic;
    }

    public void eliminarReliquia(Long relicId, Long usuarioId) {
        Relic relic = obtenerPorId(relicId, usuarioId); // Reutilizamos el método anterior que ya valida la seguridad
        relicRepository.delete(relic);
    }
}