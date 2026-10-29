package com.fitness.platform.recommendation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class RecommendationServiceException extends RuntimeException {
    public RecommendationServiceException(String message) {
        super(message);
    }
}
