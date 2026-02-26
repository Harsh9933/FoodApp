package com.foodai.backend.service;

import com.foodai.backend.model.dto.ChatRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final LocalModelService localModelService;

    public ChatService(LocalModelService localModelService) {
        this.localModelService = localModelService;
    }

    public Flux<String> processChat(ChatRequest request) {
        return localModelService.streamChat(request.message());
    }
}