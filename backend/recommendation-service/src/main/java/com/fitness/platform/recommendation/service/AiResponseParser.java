package com.fitness.platform.recommendation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.platform.recommendation.dto.RecommendationDto;
import org.springframework.stereotype.Component;

@Component
public class AiResponseParser {
    private final ObjectMapper objectMapper;
    
    public AiResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public RecommendationDto parseResponse(String aiResponse) {
        try {
            String json = extractJson(aiResponse);
            return objectMapper.readValue(json, RecommendationDto.class);
        } catch (Exception e) {
            return createFallbackRecommendation(aiResponse);
        }
    }
    
    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
    
    private RecommendationDto createFallbackRecommendation(String rawText) {
        RecommendationDto fallback = new RecommendationDto();
        fallback.setSummary("Recommendations generated but could not be structured properly.");
        fallback.setRecoveryAdvice(rawText);
        return fallback;
    }
}
