package com.foodai.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.backend.config.LocalModelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class LocalModelService {

    private static final Logger log = LoggerFactory.getLogger(LocalModelService.class);

    private final LocalModelConfig config;
    private final PromptService promptService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public LocalModelService(LocalModelConfig config, PromptService promptService) {
        this.config = config;
        this.promptService = promptService;
        this.webClient = WebClient.builder()
            .baseUrl(config.getBaseUrl())
            .build();
        this.objectMapper = new ObjectMapper();
        log.info("Local model service initialized with model: {} at {}", config.getModel(), config.getBaseUrl());
    }

    public Flux<String> streamChat(String userMessage) {
        String systemPrompt = promptService.getSystemPrompt();

        Map<String, Object> requestBody = Map.of(
            "model", config.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ),
            "stream", true
        );

        return webClient.post()
            .uri("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(String.class)
            .map(this::extractContent)
            .filter(content -> content != null && !content.isEmpty());
    }

    private String extractContent(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode message = root.path("message");
            if (message.has("content")) {
                return message.get("content").asText();
            }
            return null;
        } catch (Exception e) {
            log.debug("Could not parse response chunk: {}", jsonResponse);
            return null;
        }
    }
}