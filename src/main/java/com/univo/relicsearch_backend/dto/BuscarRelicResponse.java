package com.univo.relicsearch_backend.dto;

import com.univo.relicsearch_backend.models.BuscarRelic;
import java.time.LocalDateTime;

public class BuscarRelicResponse {
    private Long id;
    private String titulo;
    private String url;
    private String contenido;
    private LocalDateTime fechaExtraccion;

    public BuscarRelicResponse(BuscarRelic buscarRelic) {
        this.id = buscarRelic.getId();
        this.titulo = buscarRelic.getTitulo();
        this.url = buscarRelic.getUrl();
        this.contenido = buscarRelic.getContenido();
        this.fechaExtraccion = buscarRelic.getFechaExtraccion();
    }

    // Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getUrl() { return url; }
    public String getContenido() { return contenido; }
    public LocalDateTime getFechaExtraccion() { return fechaExtraccion; }
}