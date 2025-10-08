package com.zm.blog.article.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleSampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sampleShouldReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/articles/sample"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Spring Boot 微服务架构最佳实践"))
            .andExpect(jsonPath("$.data.content").value("在构建现代微服务应用时，Spring Boot 提供了强大的框架支持。通过合理的架构设计，我们可以实现高可用、可扩展的分布式系统。本文将深入探讨如何利用 Spring Boot 的特性来构建生产级的微服务应用，包括服务发现、配置管理、负载均衡等关键技术的实现方案。"))
            .andExpect(jsonPath("$.data.authorId").value(100))
            .andExpect(jsonPath("$.data.createdAt").exists());
    }
}