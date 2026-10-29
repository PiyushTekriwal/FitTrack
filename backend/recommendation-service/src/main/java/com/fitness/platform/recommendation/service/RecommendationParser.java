package com.fitness.platform.recommendation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.platform.recommendation.dto.RecommendationDto;
import org.springframework.stereotype.Component;

@Component
public class RecommendationParser {
    private final ObjectMapper objectMapper;
    
    public RecommendationParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public RecommendationDto parseResponse(String rawResponse) {
        try {
            String json = extractJson(rawResponse);
            return objectMapper.readValue(json, RecommendationDto.class);
        } catch (Exception e) {
            return createFallbackRecommendation(rawResponse);
        }
    }
    
    private String extractJson(String text) {
        if (text == null) return "{}";
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
    
    private RecommendationDto createFallbackRecommendation(String rawText) {
        RecommendationDto fallback = new RecommendationDto();
        fallback.setSummary("Personalized fitness and nutrition recommendation generated.");
        fallback.setRecoveryAdvice(rawText);
        return fallback;
    }
}
