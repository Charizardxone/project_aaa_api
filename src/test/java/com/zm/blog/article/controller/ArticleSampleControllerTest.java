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
            .andExpect(jsonPath("$.message").value("success"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Sample Title"))
            .andExpect(jsonPath("$.data.content").value("Sample Content"))
            .andExpect(jsonPath("$.data.authorId").value(100))
            .andExpect(jsonPath("$.data.createdAt").exists());
    }
}