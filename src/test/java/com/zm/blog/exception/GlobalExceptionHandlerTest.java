package com.zm.blog.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleBizExceptionShouldReturnCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/test/biz")
                .param("code", "1001")
                .param("message", "Test business error"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(1001))
            .andExpect(jsonPath("$.message").value("Test business error"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void handleIllegalArgumentExceptionShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/test/illegal")
                .param("param", ""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void handleRuntimeExceptionShouldReturn500() throws Exception {
        mockMvc.perform(get("/api/test/runtime"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(jsonPath("$.message").value("INTERNAL_ERROR"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }
}