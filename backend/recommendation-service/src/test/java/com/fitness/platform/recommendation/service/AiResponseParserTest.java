package com.fitness.platform.recommendation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fitness.platform.recommendation.dto.RecommendationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AiResponseParserTest {

    private AiResponseParser parser;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        parser = new AiResponseParser(mapper);
    }

    @Test
    void testParseResponseWithValidJson() {
        String json = "```json\n" +
                "{\n" +
                "  \"summary\": \"Great job\",\n" +
                "  \"workoutRecommendations\": [],\n" +
                "  \"dietRecommendations\": [],\n" +
                "  \"weeklyPlan\": [],\n" +
                "  \"recoveryAdvice\": \"Rest well\"\n" +
                "}\n" +
                "```";
        
        RecommendationDto dto = parser.parseResponse(json);
        assertNotNull(dto);
        assertEquals("Great job", dto.getSummary());
        assertEquals("Rest well", dto.getRecoveryAdvice());
    }

    @Test
    void testParseResponseWithInvalidJson() {
        String invalidJson = "This is not json";
        
        RecommendationDto dto = parser.parseResponse(invalidJson);
        assertNotNull(dto);
        assertEquals("Recommendations generated but could not be structured properly.", dto.getSummary());
        assertEquals("This is not json", dto.getRecoveryAdvice());
    }
}
