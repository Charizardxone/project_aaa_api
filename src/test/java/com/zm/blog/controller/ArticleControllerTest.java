package com.zm.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zm.blog.config.TestSecurityConfig;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
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
    private ArticleEditRequest validEditRequest;
    private ArticleResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";

        validRequest = new ArticleCreateRequest();
        validRequest.setTitle("Test Article Title");
        validRequest.setContent("This is a test article content");
        validRequest.setSummary("Test summary");
        validRequest.setTags("test,article");

        validEditRequest = new ArticleEditRequest();
        validEditRequest.setTitle("Updated Article Title");
        validEditRequest.setContent("This is updated article content");
        validEditRequest.setSummary("Updated summary");
        validEditRequest.setTags("updated,article");
        validEditRequest.setUpdatedAt(LocalDateTime.now());

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

    // ===== Article Edit Integration Tests =====

    @Test
    void editArticle_WithValidRequestAndToken_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        ArticleResponse updatedResponse = new ArticleResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Article Title");
        updatedResponse.setContent("This is updated article content");
        updatedResponse.setSummary("Updated summary");
        updatedResponse.setTags("updated,article");
        updatedResponse.setStatus("draft");
        updatedResponse.setAuthorId(1L);
        updatedResponse.setAuthorName("testuser");
        updatedResponse.setUpdatedAt(LocalDateTime.now());

        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), eq(1L)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEditRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("文章编辑成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated Article Title"))
                .andExpect(jsonPath("$.data.content").value("This is updated article content"))
                .andExpect(jsonPath("$.data.authorId").value(1))
                .andExpect(jsonPath("$.data.authorName").value("testuser"));

        verify(articleService).editArticle(eq(1L), any(ArticleEditRequest.class), eq(1L));
    }

    @Test
    void editArticle_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEditRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未认证"));

        verify(articleService, never()).editArticle(anyLong(), any(ArticleEditRequest.class), anyLong());
    }

    @Test
    void editArticle_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEditRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Token无效或已过期"));

        verify(articleService, never()).editArticle(anyLong(), any(ArticleEditRequest.class), anyLong());
    }

    @Test
    void editArticle_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(999L), any(ArticleEditRequest.class), eq(1L)))
                .thenThrow(new RuntimeException("文章不存在"));

        // Act & Assert
        mockMvc.perform(put("/api/articles/999")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEditRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文章不存在"));

        verify(articleService).editArticle(eq(999L), any(ArticleEditRequest.class), eq(1L));
    }

    @Test
    void editArticle_WithUnauthorizedUser_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), eq(1L)))
                .thenThrow(new RuntimeException("无权限编辑此文章"));

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEditRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("无权限编辑此文章"));

        verify(articleService).editArticle(eq(1L), any(ArticleEditRequest.class), eq(1L));
    }

    @Test
    void editArticle_WithOptimisticLockConflict_ShouldReturnConflict() throws Exception {
        // Arrange
        when(articleService.editArticle(eq(1L), any(ArticleEditRequest.class), eq(1L)))
                .thenThrow(new RuntimeException("文章已被其他用户修改，请刷新后重试"));

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEditRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("文章已被其他用户修改，请刷新后重试"));

        verify(articleService).editArticle(eq(1L), any(ArticleEditRequest.class), eq(1L));
    }

    @Test
    void editArticle_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ArticleEditRequest invalidRequest = new ArticleEditRequest();
        invalidRequest.setTitle(""); // Empty title should trigger validation error
        invalidRequest.setContent("Valid content");
        invalidRequest.setUpdatedAt(LocalDateTime.now());

        // Act & Assert
        mockMvc.perform(put("/api/articles/1")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(articleService, never()).editArticle(anyLong(), any(ArticleEditRequest.class), anyLong());
    }
}