package com.zm.blog.controller;

import com.zm.blog.dto.CommonResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public CommonResp<Map<String, Long>> health() {
        return CommonResp.success(Map.of("ts", System.currentTimeMillis()));
    }
}