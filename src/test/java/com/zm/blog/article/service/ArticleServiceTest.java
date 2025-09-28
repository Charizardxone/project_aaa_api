package com.zm.blog.article.service;

import com.zm.blog.article.dto.ArticleCreateRequest;
import com.zm.blog.article.dto.ArticleResponse;
import com.zm.blog.article.entity.Article;
import com.zm.blog.article.mapper.ArticleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleService articleService;

    private ArticleCreateRequest createRequest;
    private Article article;

    @BeforeEach
    void setUp() {
        createRequest = new ArticleCreateRequest();
        createRequest.setTitle("Test Title");
        createRequest.setContent("Test Content");
        createRequest.setSummary("Test Summary");
        createRequest.setTags("test,java");

        article = new Article();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setTags("test,java");
        article.setStatus("draft");
        article.setAuthorId(1L);
        article.setAuthorName("testuser");
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createArticle_Success() {
        // Arrange
        when(articleMapper.insert(any(Article.class))).thenReturn(1);
        // Mock the article ID generation
        Article createdArticle = new Article();
        createdArticle.setId(1L);
        createdArticle.setTitle("Test Title");
        createdArticle.setContent("Test Content");
        createdArticle.setSummary("Test Summary");
        createdArticle.setTags("test,java");
        createdArticle.setStatus("draft");
        createdArticle.setAuthorId(1L);
        createdArticle.setAuthorName("testuser");
        createdArticle.setCreatedAt(LocalDateTime.now());
        createdArticle.setUpdatedAt(LocalDateTime.now());

        when(articleMapper.selectById(1L)).thenReturn(createdArticle);

        // Act
        ArticleResponse response = articleService.createArticle(createRequest, 1L, "testuser", "test-key-123");

        // Assert
        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("draft", response.getStatus());
        assertEquals(1L, response.getAuthorId());
        assertEquals("testuser", response.getAuthorName());

        // Verify
        verify(articleMapper, times(1)).insert(any(Article.class));
    }

    @Test
    void createArticle_WithXSS_SanitizesInput() {
        // Arrange
        createRequest.setTitle("<script>alert('xss')</script>Title");
        createRequest.setContent("Content with <img src=x onerror=alert('xss')>");
        when(articleMapper.insert(any(Article.class))).thenReturn(1);
        when(articleMapper.selectById(1L)).thenReturn(article);

        // Act
        ArticleResponse response = articleService.createArticle(createRequest, 1L, "testuser", "test-key-456");

        // Assert
        assertNotNull(response);
        assertFalse(response.getTitle().contains("<script>"));
        assertFalse(response.getContent().contains("onerror="));
    }

    @Test
    void createArticle_DuplicateSubmission_ReturnsExistingArticle() {
        // Arrange
        when(articleMapper.selectById(1L)).thenReturn(article);

        // First call
        when(articleMapper.insert(any(Article.class))).thenReturn(1);

        // First submission
        ArticleResponse response1 = articleService.createArticle(createRequest, 1L, "testuser", "duplicate-key");

        // Second submission with same key
        ArticleResponse response2 = articleService.createArticle(createRequest, 1L, "testuser", "duplicate-key");

        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(response1.getId(), response2.getId());

        // Verify insert was called only once
        verify(articleMapper, times(1)).insert(any(Article.class));
    }

    @Test
    void createArticle_InsertFailure_ThrowsException() {
        // Arrange
        when(articleMapper.insert(any(Article.class))).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.createArticle(createRequest, 1L, "testuser", "test-key-fail");
        });

        assertEquals("Failed to create article", exception.getMessage());
    }
}