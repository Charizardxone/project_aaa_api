package com.zm.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public CommonResp<Map<String, Long>> health() {
        long timestamp = System.currentTimeMillis();
        Map<String, Long> data = Map.of("ts", timestamp);
        return CommonResp.success(data);
    }
}