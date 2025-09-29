package com.zm.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zm.blog.config.TestSecurityConfig;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.service.ArticleService;
import com.zm.blog.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ArticleController
 */
@WebMvcTest(ArticleController.class)
@Import(TestSecurityConfig.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private JwtUtil jwtUtil;

    private String validToken;
    private ArticleCreateRequest validRequest;
    private ArticleResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";

        validRequest = new ArticleCreateRequest();
        validRequest.setTitle("Test Article Title");
        validRequest.setContent("This is a test article content");
        validRequest.setSummary("Test summary");
        validRequest.setTags("test,article");

        sampleResponse = new ArticleResponse();
        sampleResponse.setId(1L);
        sampleResponse.setTitle("Test Article Title");
        sampleResponse.setContent("This is a test article content");
        sampleResponse.setSummary("Test summary");
        sampleResponse.setTags("test,article");
        sampleResponse.setStatus("draft");
        sampleResponse.setAuthorId(1L);
        sampleResponse.setAuthorName("testuser");
        sampleResponse.setViewCount(0);
        sampleResponse.setLikeCount(0);
        sampleResponse.setCommentCount(0);
        sampleResponse.setCreatedAt(LocalDateTime.now());
        sampleResponse.setUpdatedAt(LocalDateTime.now());

        // Mock JWT validation
        when(jwtUtil.validateToken("valid-jwt-token")).thenReturn(true);
        when(jwtUtil.getUserIdFromToken("valid-jwt-token")).thenReturn(1L);
    }

    @Test
    void createArticle_WithValidRequestAndToken_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        when(articleService.createArticle(any(ArticleCreateRequest.class), anyLong()))
                .thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("文章创建成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Article Title"))
                .andExpect(jsonPath("$.data.status").value("draft"))
                .andExpect(jsonPath("$.data.authorId").value(1))
                .andExpect(jsonPath("$.data.authorName").value("testuser"));

        verify(articleService).createArticle(any(ArticleCreateRequest.class), eq(1L));
    }

    @Test
    void getArticle_WithValidId_ShouldReturnArticle() throws Exception {
        // Arrange
        when(articleService.getArticleById(1L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/articles/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Article Title"));

        verify(articleService).getArticleById(1L);
    }
}