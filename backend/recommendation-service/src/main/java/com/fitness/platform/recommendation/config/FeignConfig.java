package com.fitness.platform.recommendation.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestRelayInterceptor() {
        return requestTemplate -> {
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String userId = request.getHeader("X-User-Id");
                    if (userId != null && !userId.isBlank()) {
                        requestTemplate.header("X-User-Id", userId);
                    }
                    String userEmail = request.getHeader("X-User-Email");
                    if (userEmail != null && !userEmail.isBlank()) {
                        requestTemplate.header("X-User-Email", userEmail);
                    }
                    String userName = request.getHeader("X-User-Name");
                    if (userName != null && !userName.isBlank()) {
                        requestTemplate.header("X-User-Name", userName);
                    }
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && !authHeader.isBlank()) {
                        requestTemplate.header("Authorization", authHeader);
                    }
                }
            } catch (Exception ignored) {}

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                requestTemplate.header("Authorization", "Bearer " + jwtAuth.getToken().getTokenValue());
            }
        };
    }
}
