package com.univo.relicsearch_backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MediaWikiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper; // Nueva herramienta para leer JSON

    public MediaWikiService() {
        this.webClient = WebClient.create("https://wiki.warframe.com");
        this.objectMapper = new ObjectMapper(); // La inicializamos
    }

    public String[] obtenerContenidoReliquia(String nombreReliquia) {
        String titleParam = nombreReliquia.replace(" ", "_");

        // 1. Pedimos la respuesta como un simple String en lugar de JsonNode
        String responseString = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api.php")
                        .queryParam("action",   "query")
                        .queryParam("prop", "extracts")
                        .queryParam("explaintext", "1")
                        .queryParam("redirects", "1")
                        .queryParam("titles", titleParam)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode response = objectMapper.readTree(responseString);

            if (response != null && response.has("query") && response.get("query").has("pages")) {
                JsonNode pages = response.get("query").get("pages");

                var elements = pages.elements();
                if (elements.hasNext()) {
                    JsonNode page = elements.next();

                    // --- EL CAMBIO ESTÁ AQUÍ ---
                    // Si la página no existe, devolvemos un mensaje en lugar de lanzar un error 500
                    if (page.has("missing")) {
                        return new String[]{
                                nombreReliquia,
                                "La reliquia '" + nombreReliquia + "' no existe en la base de datos de la Wiki de Warframe."
                        };
                    }

                    String titulo = page.has("title") ? page.get("title").asText() : nombreReliquia;
                    String contenido = page.has("extract") && !page.get("extract").isNull()
                            ? page.get("extract").asText()
                            : "No se encontró contenido de texto para este artículo.";

                    return new String[]{titulo, contenido};
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el JSON de la Wiki: " + e.getMessage());
        }

        throw new RuntimeException("Error al obtener la respuesta de MediaWiki.");
    }
}