package com.foodai.backend.service;

import com.foodai.backend.config.GeminiConfig;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final GeminiConfig config;
    private final PromptService promptService;
    private Client client;

    public GeminiService(GeminiConfig config, PromptService promptService) {
        this.config = config;
        this.promptService = promptService;
        initClient();
    }

    private void initClient() {
        if (config.getApiKey() != null && !config.getApiKey().isBlank()) {
            this.client = Client.builder().apiKey(config.getApiKey()).build();
            log.info("Gemini client initialized with model: {}", config.getModel());
        } else {
            log.warn("Gemini API key not configured. Set GEMINI_API_KEY env variable.");
        }
    }

    public Flux<String> streamChat(String userMessage) {
        if (client == null) {
            return Flux.just("AI service is not configured. Please set GEMINI_API_KEY.");
        }

        return Flux.create(emitter -> {
            try {
                String systemPrompt = promptService.getSystemPrompt();

                GenerateContentConfig contentConfig = GenerateContentConfig.builder()
                    .systemInstruction(Content.fromParts(Part.fromText(systemPrompt)))
                    .build();

                Content userContent = Content.fromParts(Part.fromText(userMessage));

                client.models.generateContentStream(
                    config.getModel(),
                    userContent,
                    contentConfig
                ).forEach(chunk -> {
                    if (chunk != null && chunk.text() != null) {
                        emitter.next(chunk.text());
                    }
                });

                emitter.complete();
            } catch (Exception e) {
                log.error("Error streaming from Gemini", e);
                emitter.error(new RuntimeException("Failed to get AI response: " + e.getMessage()));
            }
        });
    }
}
