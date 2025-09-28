package com.zm.blog.controller;

import com.zm.blog.dto.ApiResponse;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文章控制器测试类
 */
@WebMvcTest(ArticleController.class)
@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    private ArticleCreateRequest createRequest;
    private ArticleEditRequest editRequest;
    private ArticleResponse articleResponse;

    @BeforeEach
    void setUp() {
        createRequest = new ArticleCreateRequest();
        createRequest.setTitle("测试标题");
        createRequest.setContent("测试内容");
        createRequest.setSummary("测试摘要");
        createRequest.setTags("测试标签");

        editRequest = new ArticleEditRequest();
        editRequest.setTitle("更新标题");
        editRequest.setContent("更新内容");
        editRequest.setSummary("更新摘要");
        editRequest.setTags("更新标签");
        editRequest.setUpdatedAt(LocalDateTime.now());

        articleResponse = new ArticleResponse();
        articleResponse.setId(1L);
        articleResponse.setTitle("测试标题");
        articleResponse.setContent("测试内容");
        articleResponse.setStatus("draft");
        articleResponse.setAuthorId(1L);
        articleResponse.setCreatedAt(LocalDateTime.now());
        articleResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void createArticle_Success() throws Exception {
        // Arrange
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
                .thenReturn(articleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("测试标题"))
                .andExpect(jsonPath("$.data.content").value("测试内容"));

        verify(articleService, times(1)).createArticle(any(ArticleCreateRequest.class), any(Long.class));
    }

    @Test
    @WithMockUser
    void createArticle_InvalidInput() throws Exception {
        // Arrange
        createRequest.setTitle(""); // 空标题

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).createArticle(any(ArticleCreateRequest.class), any(Long.class));
    }

    @Test
    @WithMockUser
    void createArticle_TitleTooLong() throws Exception {
        // Arrange
        createRequest.setTitle("a".repeat(101)); // 101字符

        // Act & Assert
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).createArticle(any(ArticleCreateRequest.class), any(Long.class));
    }

    @Test
    @WithMockUser
    void editArticle_Success() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class)))
                .thenReturn(articleResponse);

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(articleService, times(1)).editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class));
    }

    @Test
    @WithMockUser
    void editArticle_NotFound() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class)))
                .thenThrow(new RuntimeException("404"));

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isNotFound());

        verify(articleService, times(1)).editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class));
    }

    @Test
    @WithMockUser
    void editArticle_NoPermission() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class)))
                .thenThrow(new RuntimeException("403"));

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isForbidden());

        verify(articleService, times(1)).editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class));
    }

    @Test
    @WithMockUser
    void editArticle_ConcurrentConflict() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class)))
                .thenThrow(new RuntimeException("409"));

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isConflict());

        verify(articleService, times(1)).editArticle(eq(1L), any(ArticleEditRequest.class), any(Long.class));
    }

    @Test
    void getArticle_Success() throws Exception {
        // Arrange
        when(articleService.getArticleById(1L)).thenReturn(articleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试标题"));

        verify(articleService, times(1)).getArticleById(1L);
    }

    @Test
    void getArticle_NotFound() throws Exception {
        // Arrange
        when(articleService.getArticleById(1L))
                .thenThrow(new RuntimeException("404"));

        // Act & Assert
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isNotFound());

        verify(articleService, times(1)).getArticleById(1L);
    }

    @Test
    void createArticle_Authentication() throws Exception {
        // Act & Assert - 未认证用户应该能够创建文章（因为配置了permitAll）
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }
}