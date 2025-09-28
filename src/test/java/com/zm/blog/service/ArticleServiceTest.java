package com.zm.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.blog.common.BusinessException;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.mapper.ArticleMapper;
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

/**
 * 文章服务测试类
 */
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleService articleService;

    private ArticleCreateRequest createRequest;
    private ArticleEditRequest editRequest;
    private Article existingArticle;

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

        existingArticle = new Article();
        existingArticle.setId(1L);
        existingArticle.setTitle("原始标题");
        existingArticle.setContent("原始内容");
        existingArticle.setAuthorId(1L);
        existingArticle.setStatus("draft");
        existingArticle.setCreatedAt(LocalDateTime.now());
        existingArticle.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createArticle_Success() {
        // Arrange
        when(articleMapper.insert(any(Article.class))).thenReturn(1);
        when(articleMapper.selectById(any())).thenReturn(existingArticle);

        // Act
        ArticleResponse response = articleService.createArticle(createRequest, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("测试标题", response.getTitle());
        assertEquals("测试内容", response.getContent());
        verify(articleMapper, times(1)).insert(any(Article.class));
    }

    @Test
    void createArticle_Failed() {
        // Arrange
        when(articleMapper.insert(any(Article.class))).thenReturn(0);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            articleService.createArticle(createRequest, 1L);
        });
    }

    @Test
    void createArticle_WithXSS_Attack() {
        // Arrange
        createRequest.setTitle("<script>alert('xss')</script>标题");
        createRequest.setContent("内容<script>alert('xss')</script>");
        when(articleMapper.insert(any(Article.class))).thenReturn(1);
        when(articleMapper.selectById(any())).thenReturn(existingArticle);

        // Act
        ArticleResponse response = articleService.createArticle(createRequest, 1L);

        // Assert
        assertNotNull(response);
        assertFalse(response.getTitle().contains("<script>"), "XSS脚本应该被过滤");
        assertFalse(response.getContent().contains("<script>"), "XSS脚本应该被过滤");
    }

    @Test
    void createArticle_WithSQL_Injection() {
        // Arrange
        createRequest.setTitle("标题'; DROP TABLE users; --");
        when(articleMapper.insert(any(Article.class))).thenReturn(1);
        when(articleMapper.selectById(any())).thenReturn(existingArticle);

        // Act
        ArticleResponse response = articleService.createArticle(createRequest, 1L);

        // Assert
        assertNotNull(response);
        assertFalse(response.getTitle().contains("DROP TABLE"), "SQL注入应该被过滤");
    }

    @Test
    void editArticle_Success() {
        // Arrange
        when(articleMapper.selectById(1L)).thenReturn(existingArticle);
        when(articleMapper.updateById(any(Article.class))).thenReturn(1);

        // Act
        ArticleResponse response = articleService.editArticle(1L, editRequest, 1L);

        // Assert
        assertNotNull(response);
        verify(articleMapper, times(1)).selectById(1L);
        verify(articleMapper, times(1)).updateById(any(Article.class));
    }

    @Test
    void editArticle_NotFound() {
        // Arrange
        when(articleMapper.selectById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            articleService.editArticle(1L, editRequest, 1L);
        });
        assertEquals(404, exception.getCode());
    }

    @Test
    void editArticle_NoPermission() {
        // Arrange
        existingArticle.setAuthorId(2L); // 不同作者
        when(articleMapper.selectById(1L)).thenReturn(existingArticle);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            articleService.editArticle(1L, editRequest, 1L);
        });
        assertEquals(403, exception.getCode());
    }

    @Test
    void editArticle_ConcurrentConflict() {
        // Arrange
        existingArticle.setUpdatedAt(LocalDateTime.now().minusMinutes(1)); // 旧版本
        when(articleMapper.selectById(1L)).thenReturn(existingArticle);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            articleService.editArticle(1L, editRequest, 1L);
        });
        assertEquals(409, exception.getCode());
    }

    @Test
    void getArticleById_Success() {
        // Arrange
        when(articleMapper.selectWithAuthor(1L)).thenReturn(existingArticle);

        // Act
        ArticleResponse response = articleService.getArticleById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("原始标题", response.getTitle());
    }

    @Test
    void getArticleById_NotFound() {
        // Arrange
        when(articleMapper.selectWithAuthor(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            articleService.getArticleById(1L);
        });
        assertEquals(404, exception.getCode());
    }
}