package com.zm.blog.controller;

import com.zm.blog.dto.ApiResponse;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@WithMockUser(username = "testuser", roles = "USER")
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    private ArticleCreateRequest validRequest;
    private ArticleResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ArticleCreateRequest();
        validRequest.setTitle("Test Title");
        validRequest.setContent("Test Content");
        validRequest.setSummary("Test Summary");
        validRequest.setTags("test,article");

        mockResponse = new ArticleResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Test Title");
        mockResponse.setContent("Test Content");
        mockResponse.setSummary("Test Summary");
        mockResponse.setTags("test,article");
        mockResponse.setStatus("DRAFT");
        mockResponse.setAuthorId(1L);
        mockResponse.setAuthorName("Test User");
        mockResponse.setCreatedAt(LocalDateTime.now());
        mockResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createArticle_Success() throws Exception {
        // Arrange
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    void createArticle_InvalidRequest_MissingTitle() throws Exception {
        // Arrange
        ArticleCreateRequest invalidRequest = new ArticleCreateRequest();
        invalidRequest.setContent("Test Content");
        invalidRequest.setSummary("Test Summary");

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("标题不能为空"));
    }

    @Test
    void createArticle_InvalidRequest_MissingContent() throws Exception {
        // Arrange
        ArticleCreateRequest invalidRequest = new ArticleCreateRequest();
        invalidRequest.setTitle("Test Title");
        invalidRequest.setSummary("Test Summary");

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("正文不能为空"));
    }

    @Test
    void createArticle_InvalidRequest_TitleTooLong() throws Exception {
        // Arrange
        ArticleCreateRequest invalidRequest = new ArticleCreateRequest();
        invalidRequest.setTitle("a".repeat(101)); // 101 characters
        invalidRequest.setContent("Test Content");

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("标题长度不能超过100个字符"));
    }

    @Test
    void createArticle_InvalidRequest_ContentTooLong() throws Exception {
        // Arrange
        ArticleCreateRequest invalidRequest = new ArticleCreateRequest();
        invalidRequest.setTitle("Test Title");
        invalidRequest.setContent("a".repeat(10001)); // 10001 characters

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("正文长度不能超过10000个字符"));
    }

    @Test
    void createArticle_InvalidRequest_SummaryTooLong() throws Exception {
        // Arrange
        ArticleCreateRequest invalidRequest = new ArticleCreateRequest();
        invalidRequest.setTitle("Test Title");
        invalidRequest.setContent("Test Content");
        invalidRequest.setSummary("a".repeat(301)); // 301 characters

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("摘要长度不能超过300个字符"));
    }

    @Test
    void createArticle_InvalidRequest_TagsTooLong() throws Exception {
        // Arrange
        ArticleCreateRequest invalidRequest = new ArticleCreateRequest();
        invalidRequest.setTitle("Test Title");
        invalidRequest.setContent("Test Content");
        invalidRequest.setTags("a".repeat(201)); // 201 characters

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("标签长度不能超过200个字符"));
    }

    @Test
    void createArticle_XssProtection() throws Exception {
        // Arrange
        ArticleCreateRequest xssRequest = new ArticleCreateRequest();
        xssRequest.setTitle("<script>alert('xss')</script>Test Title");
        xssRequest.setContent("Test <script>alert('xss')</script>Content");
        xssRequest.setSummary("<script>alert('xss')</script>Summary");

        ArticleResponse cleanedResponse = new ArticleResponse();
        cleanedResponse.setId(1L);
        cleanedResponse.setTitle("Test Title"); // Cleaned
        cleanedResponse.setContent("Test Content"); // Cleaned
        cleanedResponse.setSummary("Summary"); // Cleaned
        cleanedResponse.setStatus("DRAFT");
        cleanedResponse.setAuthorId(1L);
        cleanedResponse.setAuthorName("Test User");

        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
                .thenReturn(cleanedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.content").value("Test Content"))
                .andExpect(jsonPath("$.data.summary").value("Summary"));
    }
}