package com.foodai.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        // Build response in a Java 8-compatible way to avoid runtime issues
        Map<String, String> body = new java.util.HashMap<>();
        body.put("status", "ok");
        body.put("model", "tinyllama");
        return ResponseEntity.ok(body);
    }
}
