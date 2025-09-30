package com.zm.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class HealthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void health_shouldReturnCorrectResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.ts").exists());
    }

    @Test
    void health_shouldReturnJsonStructure() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.ts").isString());
    }

    @Test
    void health_shouldReturnFormattedDateTime() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ts").exists())
                .andExpect(jsonPath("$.data.ts").isString())
                .andExpect(mvcResult -> {
                    String response = mvcResult.getResponse().getContentAsString();

                    // Extract timestamp string from JSON response
                    // The response format is: {"code":0,"message":"OK","data":{"ts":"2025-01-01 12:00:00"}}
                    int tsStart = response.indexOf("\"ts\":\"") + 6;
                    int tsEnd = response.indexOf("\"", tsStart);
                    String timestamp = response.substring(tsStart, tsEnd);

                    // Validate format matches yyyy-MM-dd HH:mm:ss pattern
                    if (!timestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                        throw new AssertionError("Timestamp format should be yyyy-MM-dd HH:mm:ss, but was: " + timestamp);
                    }
                });
    }
}