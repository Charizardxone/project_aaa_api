package com.zm.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Health check controller for service monitoring
 */
@RestController
public class HealthController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Health check endpoint to verify service is running
     * @return Map containing health status with formatted timestamp
     */
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        long now = System.currentTimeMillis();
        String formatted = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        return Map.of(
            "code", 0,
            "message", "OK",
            "timestamp", now,
            "data", Map.of("ts", formatted)
        );
        
    }
}