package com.zm.blog.service;

import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
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

import java.time.LocalDateTime;
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
    private ArticleEditRequest validEditRequest;
    private Article existingArticle;

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

        // Set up edit request
        validEditRequest = new ArticleEditRequest();
        validEditRequest.setTitle("Updated Article Title");
        validEditRequest.setContent("This is updated article content");
        validEditRequest.setSummary("Updated summary");
        validEditRequest.setTags("updated,article");
        validEditRequest.setUpdatedAt(LocalDateTime.now());

        // Set up existing article
        existingArticle = new Article();
        existingArticle.setId(1L);
        existingArticle.setTitle("Original Title");
        existingArticle.setContent("Original content");
        existingArticle.setSummary("Original summary");
        existingArticle.setTags("original,tags");
        existingArticle.setStatus("draft");
        existingArticle.setAuthorId(1L);
        existingArticle.setUpdatedAt(validEditRequest.getUpdatedAt());
        existingArticle.setAuthor(testUser);
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

    // ===== Article Edit Tests =====

    @Test
    void editArticle_WithValidRequest_ShouldReturnUpdatedArticle() {
        // Arrange
        when(articleRepository.findArticleWithAuthor(1L)).thenReturn(existingArticle);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setUpdatedAt(LocalDateTime.now().plusMinutes(1)); // Simulate timestamp update
            return article;
        });

        // Act
        ArticleResponse result = articleService.editArticle(1L, validEditRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Article Title", result.getTitle());
        assertEquals("This is updated article content", result.getContent());
        assertEquals("Updated summary", result.getSummary());
        assertEquals("updated,article", result.getTags());
        assertEquals("testuser", result.getAuthorName());

        verify(articleRepository).findArticleWithAuthor(1L);
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void editArticle_WithNonExistentArticle_ShouldThrowException() {
        // Arrange
        when(articleRepository.findArticleWithAuthor(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> articleService.editArticle(999L, validEditRequest, 1L));

        assertEquals("文章不存在", exception.getMessage());
        verify(articleRepository).findArticleWithAuthor(999L);
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void editArticle_WithUnauthorizedUser_ShouldThrowException() {
        // Arrange
        when(articleRepository.findArticleWithAuthor(1L)).thenReturn(existingArticle);

        // Act & Assert - Try to edit with different user ID (2L instead of 1L)
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> articleService.editArticle(1L, validEditRequest, 2L));

        assertEquals("无权限编辑此文章", exception.getMessage());
        verify(articleRepository).findArticleWithAuthor(1L);
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void editArticle_WithOptimisticLockFailure_ShouldThrowException() {
        // Arrange
        Article outdatedArticle = new Article();
        outdatedArticle.setId(1L);
        outdatedArticle.setAuthorId(1L);
        outdatedArticle.setUpdatedAt(LocalDateTime.now().minusHours(1)); // Different timestamp
        outdatedArticle.setAuthor(testUser);

        when(articleRepository.findArticleWithAuthor(1L)).thenReturn(outdatedArticle);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> articleService.editArticle(1L, validEditRequest, 1L));

        assertTrue(exception.getMessage().contains("文章已被其他用户修改"));
        verify(articleRepository).findArticleWithAuthor(1L);
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void editArticle_WithXSSContent_ShouldSanitizeInput() {
        // Arrange
        ArticleEditRequest requestWithXSS = new ArticleEditRequest();
        requestWithXSS.setTitle("<script>alert('xss')</script>Malicious Title");
        requestWithXSS.setContent("Safe content with <strong>bold</strong> text");
        requestWithXSS.setSummary("<script>alert('xss')</script>Malicious summary");
        requestWithXSS.setTags("<script>alert('xss')</script>malicious,tags");
        requestWithXSS.setUpdatedAt(existingArticle.getUpdatedAt());

        when(articleRepository.findArticleWithAuthor(1L)).thenReturn(existingArticle);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setUpdatedAt(LocalDateTime.now().plusMinutes(1));
            return article;
        });

        // Act
        ArticleResponse result = articleService.editArticle(1L, requestWithXSS, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.getTitle().contains("<script>"));
        assertFalse(result.getSummary().contains("<script>"));
        assertFalse(result.getTags().contains("<script>"));
        assertTrue(result.getContent().contains("<strong>bold</strong>")); // Allow safe HTML

        verify(articleRepository).findArticleWithAuthor(1L);
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void editArticle_WithPublishedArticle_ShouldUpdateSuccessfully() {
        // Arrange
        existingArticle.setStatus("published");
        when(articleRepository.findArticleWithAuthor(1L)).thenReturn(existingArticle);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setUpdatedAt(LocalDateTime.now().plusMinutes(1));
            return article;
        });

        // Act
        ArticleResponse result = articleService.editArticle(1L, validEditRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Article Title", result.getTitle());
        assertEquals("This is updated article content", result.getContent());
        assertEquals("published", result.getStatus()); // Status should remain published

        verify(articleRepository).findArticleWithAuthor(1L);
        verify(articleRepository).save(any(Article.class));
    }
}