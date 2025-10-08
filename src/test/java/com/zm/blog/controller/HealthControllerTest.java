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
            .andExpect(jsonPath("$.data.ts").isNumber());
    }

    @Test
    void healthShouldReturnNumericTimestamp() throws Exception {
        String response = mockMvc.perform(get("/api/health"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Verify timestamp is numeric (milliseconds since epoch)
        assert response.matches(".*\"ts\":\\d+.*");
    }

    @Test
    void healthShouldReturnRecentTimestamp() throws Exception {
        long beforeRequest = System.currentTimeMillis();
        String response = mockMvc.perform(get("/api/health"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        long afterRequest = System.currentTimeMillis();

        // Extract timestamp from response (simple parsing for test)
        long responseTimestamp = Long.parseLong(response.replaceAll(".*\"ts\":(\\d+).*", "$1"));

        // Verify timestamp is within reasonable range (within 1 second of request time)
        assert responseTimestamp >= beforeRequest - 1000;
        assert responseTimestamp <= afterRequest + 1000;
    }
}