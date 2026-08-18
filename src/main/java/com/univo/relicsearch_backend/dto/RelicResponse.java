package com.univo.relicsearch_backend.dto;

import com.univo.relicsearch_backend.models.Relic;
import java.time.LocalDateTime;

public class RelicResponse {
    private Long id;
    private String nombre;
    private String url;
    private LocalDateTime fechaCreacion;

    // Constructor que convierte la Entidad en un DTO limpio
    public RelicResponse(Relic relic) {
        this.id = relic.getId();
        this.nombre = relic.getNombre();
        this.url = relic.getUrl();
        this.fechaCreacion = relic.getFechaCreacion();
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUrl() { return url; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}