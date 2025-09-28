package com.zm.blog.article.controller;

import com.zm.blog.article.dto.ArticleCreateRequest;
import com.zm.blog.article.dto.ArticleResponse;
import com.zm.blog.article.service.ArticleService;
import com.zm.blog.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ArticleController articleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Authentication authentication;
    private ArticleCreateRequest createRequest;
    private ArticleResponse articleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
        objectMapper = new ObjectMapper();

        authentication = new UsernamePasswordAuthenticationToken("testuser", "password");

        createRequest = new ArticleCreateRequest();
        createRequest.setTitle("Test Title");
        createRequest.setContent("Test Content");
        createRequest.setSummary("Test Summary");
        createRequest.setTags("test,java");

        articleResponse = new ArticleResponse();
        articleResponse.setId(1L);
        articleResponse.setTitle("Test Title");
        articleResponse.setContent("Test Content");
        articleResponse.setSummary("Test Summary");
        articleResponse.setTags("test,java");
        articleResponse.setStatus("draft");
        articleResponse.setAuthorId(1L);
        articleResponse.setAuthorName("testuser");
    }

    @Test
    void createArticle_Success() throws Exception {
        // Arrange
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class), any(String.class), any(String.class)))
                .thenReturn(articleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Idempotency-Key", "test-key-123")
                .principal(authentication)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.status").value("draft"));
    }

    @Test
    void createArticle_InvalidRequest_ThrowsException() throws Exception {
        // Arrange
        createRequest.setTitle(""); // Invalid title
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class), any(String.class), any(String.class)))
                .thenThrow(new IllegalArgumentException("标题不能为空"));

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("标题不能为空"));
    }

    @Test
    void createArticle_ServerError_Returns500() throws Exception {
        // Arrange
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class), any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void createArticle_WithoutIdempotencyKey_GeneratesOne() throws Exception {
        // Arrange
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class), any(String.class), any(String.class)))
                .thenReturn(articleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1L));
    }
}