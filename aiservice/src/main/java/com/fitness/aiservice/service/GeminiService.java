package com.fitness.aiservice.service;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private final WebClient webClient ;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientbuilder){
        this.webClient = webClientbuilder.build();

    }

    public String getRecommendations(String details) {

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", details)
                                )
                        )
                )
        );

        return webClient.post()
                .uri(geminiApiUrl.trim())
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", geminiApiKey.trim())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    }


