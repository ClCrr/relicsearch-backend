package com.univo.relicsearch_backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // <-- NUEVO IMPORT

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public GroqService() {
        this.webClient = WebClient.create("https://api.groq.com/openai/v1");
        this.objectMapper = new ObjectMapper();
    }

    public String analizarContenido(String contenidoWiki) {

        // 1. Tomamos el contenido real y lo recortamos a 3,000 caracteres.
        // (Esto es suficiente para que la IA lea el farmeo, pero pequeño para no saturar a Groq)
        int limiteCaracteres = 3000;
        String textoSeguro = contenidoWiki;

        if (textoSeguro != null && textoSeguro.length() > limiteCaracteres) {
            textoSeguro = textoSeguro.substring(0, limiteCaracteres);
        }

        Map<String, Object> requestBody = new HashMap<>();

        // PON AQUÍ EL MODELO QUE TE FUNCIONÓ EN LA PRUEBA
        requestBody.put("model", "groq/compound");

        // 2. Armamos la instrucción para la IA usando el texto real de la Wiki
        String prompt = "Eres un asistente experto en el juego Warframe. " +
                "Analiza el siguiente texto de la wiki y responde en español: ¿Dónde y cómo puedo conseguir esta reliquia de forma resumida? " +
                "Texto a analizar: " + textoSeguro;

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.7);

        try {
            String responseString = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode responseJson = objectMapper.readTree(responseString);
            return responseJson.get("choices").get(0).get("message").get("content").asText();

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            throw new RuntimeException("Motivo exacto del rechazo de Groq: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno al conectar: " + e.getMessage());
        }
    }

}