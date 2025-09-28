package com.zm.blog.service;

import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.entity.User;
import com.zm.blog.mapper.ArticleMapper;
import com.zm.blog.mapper.UserMapper;
import com.zm.blog.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private UserMapper userMapper;

    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        articleService = new ArticleServiceImpl(articleMapper, userMapper);
    }

    @Test
    void createArticle_Success() {
        // Arrange
        Long authorId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setSummary("Test Summary");
        request.setTags("test,article");

        User user = new User();
        user.setId(authorId);
        user.setUsername("testuser");
        user.setNickname("Test User");

        Article savedArticle = new Article();
        savedArticle.setId(1L);
        savedArticle.setTitle("Test Title");
        savedArticle.setContent("Test Content");
        savedArticle.setSummary("Test Summary");
        savedArticle.setTags("test,article");
        savedArticle.setStatus("DRAFT");
        savedArticle.setAuthorId(authorId);
        savedArticle.setCreatedAt(LocalDateTime.now());
        savedArticle.setUpdatedAt(LocalDateTime.now());

        when(userMapper.selectById(authorId)).thenReturn(user);
        when(articleMapper.selectOne(any())).thenReturn(null);
        when(articleMapper.insert(any(Article.class))).thenReturn(1);

        // Act
        ArticleResponse response = articleService.createArticle(request, authorId);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("Test User", response.getAuthorName());
        assertEquals("DRAFT", response.getStatus());

        verify(userMapper).selectById(authorId);
        verify(articleMapper).insert(any(Article.class));
    }

    @Test
    void createArticle_UserNotFound() {
        // Arrange
        Long authorId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        when(userMapper.selectById(authorId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.createArticle(request, authorId);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper).selectById(authorId);
        verify(articleMapper, never()).insert(any(Article.class));
    }

    @Test
    void createArticle_DuplicateSubmission() {
        // Arrange
        Long authorId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        User user = new User();
        user.setId(authorId);
        user.setUsername("testuser");

        Article recentArticle = new Article();
        recentArticle.setId(2L);
        recentArticle.setTitle("Test Title");
        recentArticle.setCreatedAt(LocalDateTime.now());

        when(userMapper.selectById(authorId)).thenReturn(user);
        when(articleMapper.selectOne(any())).thenReturn(recentArticle);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.createArticle(request, authorId);
        });

        assertTrue(exception.getMessage().contains("文章创建过于频繁"));
        verify(userMapper).selectById(authorId);
        verify(articleMapper, never()).insert(any(Article.class));
    }

    @Test
    void createArticle_InsertFailed() {
        // Arrange
        Long authorId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        User user = new User();
        user.setId(authorId);
        user.setUsername("testuser");

        when(userMapper.selectById(authorId)).thenReturn(user);
        when(articleMapper.selectOne(any())).thenReturn(null);
        when(articleMapper.insert(any(Article.class))).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.createArticle(request, authorId);
        });

        assertEquals("文章创建失败", exception.getMessage());
        verify(userMapper).selectById(authorId);
        verify(articleMapper).insert(any(Article.class));
    }

    @Test
    void createArticle_XssFiltering() {
        // Arrange
        Long authorId = 1L;
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("<script>alert('xss')</script>Test Title");
        request.setContent("Test <script>alert('xss')</script>Content");
        request.setSummary("<script>alert('xss')</script>Summary");

        User user = new User();
        user.setId(authorId);
        user.setUsername("testuser");
        user.setNickname("Test User");

        when(userMapper.selectById(authorId)).thenReturn(user);
        when(articleMapper.selectOne(any())).thenReturn(null);
        when(articleMapper.insert(any(Article.class))).thenReturn(1);

        // Act
        ArticleResponse response = articleService.createArticle(request, authorId);

        // Assert
        assertNotNull(response);
        // The title should be cleaned (script tags removed)
        assertFalse(response.getTitle().contains("<script>"));
        assertFalse(response.getContent().contains("<script>"));
        assertFalse(response.getSummary().contains("<script>"));

        verify(articleMapper).insert(argThat(article ->
            !article.getTitle().contains("<script>") &&
            !article.getContent().contains("<script>")
        ));
    }
}