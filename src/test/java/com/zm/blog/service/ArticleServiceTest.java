package com.zm.blog.service;

import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.entity.User;
import com.zm.blog.repository.ArticleRepository;
import com.zm.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ArticleService
 */
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArticleService articleService;

    private User testUser;
    private ArticleCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        validRequest = new ArticleCreateRequest();
        validRequest.setTitle("Test Article Title");
        validRequest.setContent("This is a test article content");
        validRequest.setSummary("Test summary");
        validRequest.setTags("test,article");
    }

    @Test
    void createArticle_WithValidRequest_ShouldReturnArticleResponse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(1L);
            return article;
        });

        // Act
        ArticleResponse result = articleService.createArticle(validRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Article Title", result.getTitle());
        assertEquals("This is a test article content", result.getContent());
        assertEquals("Test summary", result.getSummary());
        assertEquals("test,article", result.getTags());
        assertEquals("draft", result.getStatus());
        assertEquals(1L, result.getAuthorId());
        assertEquals("testuser", result.getAuthorName());

        verify(userRepository).findById(1L);
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void createArticle_WithNonExistentAuthor_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> articleService.createArticle(validRequest, 1L));

        assertEquals("作者不存在", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void createArticle_WithXSSContent_ShouldSanitizeInput() {
        // Arrange
        ArticleCreateRequest requestWithXSS = new ArticleCreateRequest();
        requestWithXSS.setTitle("<script>alert('xss')</script>Safe Title");
        requestWithXSS.setContent("Safe content with <strong>bold</strong> text");
        requestWithXSS.setSummary("<script>alert('xss')</script>Safe summary");
        requestWithXSS.setTags("<script>alert('xss')</script>safe,tags");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(1L);
            return article;
        });

        // Act
        ArticleResponse result = articleService.createArticle(requestWithXSS, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.getTitle().contains("<script>"));
        assertFalse(result.getSummary().contains("<script>"));
        assertFalse(result.getTags().contains("<script>"));
        assertTrue(result.getContent().contains("<strong>bold</strong>")); // Allow safe HTML

        verify(userRepository).findById(1L);
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void createArticle_WithDuplicateSubmission_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(1L);
            return article;
        });

        // Act - Create first article
        ArticleResponse firstResult = articleService.createArticle(validRequest, 1L);
        assertNotNull(firstResult);

        // Act & Assert - Try to create duplicate
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> articleService.createArticle(validRequest, 1L));

        assertTrue(exception.getMessage().contains("重复提交"));
    }

    @Test
    void getArticleById_WithValidId_ShouldReturnArticle() {
        // Arrange
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setStatus("draft");
        article.setAuthorId(1L);

        User author = new User();
        author.setUsername("testuser");
        article.setAuthor(author);

        when(articleRepository.findArticleWithAuthor(1L)).thenReturn(article);

        // Act
        ArticleResponse result = articleService.getArticleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Article", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals("draft", result.getStatus());
        assertEquals(1L, result.getAuthorId());
        assertEquals("testuser", result.getAuthorName());

        verify(articleRepository).findArticleWithAuthor(1L);
    }

    @Test
    void getArticleById_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(articleRepository.findArticleWithAuthor(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> articleService.getArticleById(999L));

        assertEquals("文章不存在", exception.getMessage());
        verify(articleRepository).findArticleWithAuthor(999L);
    }
}