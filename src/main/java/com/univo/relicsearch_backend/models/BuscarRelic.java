package com.univo.relicsearch_backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "buscar_relics")
public class BuscarRelic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String url;

    @Column(columnDefinition = "TEXT")
    private String contenido; // Aquí guardaremos todo el texto plano de MediaWiki

    @Column(name = "fecha_extraccion", updatable = false)
    private LocalDateTime fechaExtraccion;

    // Relación: Muchas búsquedas/extracciones pertenecen a una Reliquia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relic_id", nullable = false)
    private Relic relic;

    @PrePersist
    protected void onCreate() {
        fechaExtraccion = LocalDateTime.now();
    }

    public BuscarRelic() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaExtraccion() { return fechaExtraccion; }
    public void setFechaExtraccion(LocalDateTime fechaExtraccion) { this.fechaExtraccion = fechaExtraccion; }

    public Relic getRelic() { return relic; }
    public void setRelic(Relic relic) { this.relic = relic; }
}