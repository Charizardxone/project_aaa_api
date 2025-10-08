package com.zm.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthShouldReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.ts").exists())
            .andExpect(jsonPath("$.data.ts").isString());
    }

    @Test
    void healthShouldReturnFormattedTimestamp() throws Exception {
        String response = mockMvc.perform(get("/api/health"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Verify timestamp format using regex pattern
        assert response.matches(".*\"ts\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\".*");
    }
}