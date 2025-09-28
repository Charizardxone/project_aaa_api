package com.zm.blog.service;

import com.zm.blog.dto.ArticleCreateRequest;
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

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleService articleService;

    private ArticleCreateRequest validRequest;
    private Article savedArticle;

    @BeforeEach
    void setUp() {
        validRequest = new ArticleCreateRequest(
            "Test Title",
            "Test Content",
            "Test Summary",
            "test, tags"
        );

        savedArticle = new Article();
        savedArticle.setId(1L);
        savedArticle.setTitle("Test Title");
        savedArticle.setContent("Test Content");
        savedArticle.setSummary("Test Summary");
        savedArticle.setTags("test, tags");
        savedArticle.setStatus("draft");
        savedArticle.setAuthorId(1L);
        savedArticle.setCreatedAt(LocalDateTime.now());
        savedArticle.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createArticle_Success() {
        when(articleMapper.insert(any(Article.class))).thenReturn(1);

        ArticleResponse response = articleService.createArticle(validRequest, 1L);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("draft", response.getStatus());
        assertEquals(1L, response.getAuthorId());

        verify(articleMapper, times(1)).insert(any(Article.class));
    }

    @Test
    void createArticle_Failure() {
        when(articleMapper.insert(any(Article.class))).thenReturn(0);

        assertThrows(RuntimeException.class, () -> {
            articleService.createArticle(validRequest, 1L);
        });

        verify(articleMapper, times(1)).insert(any(Article.class));
    }

    @Test
    void getArticleById_Success() {
        when(articleMapper.selectById(1L)).thenReturn(savedArticle);

        ArticleResponse response = articleService.getArticleById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Title", response.getTitle());

        verify(articleMapper, times(1)).selectById(1L);
    }

    @Test
    void getArticleById_NotFound() {
        when(articleMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            articleService.getArticleById(1L);
        });

        verify(articleMapper, times(1)).selectById(1L);
    }

    @Test
    void existsById_True() {
        when(articleMapper.selectById(1L)).thenReturn(savedArticle);

        boolean exists = articleService.existsById(1L);

        assertTrue(exists);
        verify(articleMapper, times(1)).selectById(1L);
    }

    @Test
    void existsById_False() {
        when(articleMapper.selectById(1L)).thenReturn(null);

        boolean exists = articleService.existsById(1L);

        assertFalse(exists);
        verify(articleMapper, times(1)).selectById(1L);
    }

    @Test
    void isArticleOwner_True() {
        when(articleMapper.selectById(1L)).thenReturn(savedArticle);

        boolean isOwner = articleService.isArticleOwner(1L, 1L);

        assertTrue(isOwner);
        verify(articleMapper, times(1)).selectById(1L);
    }

    @Test
    void isArticleOwner_False() {
        when(articleMapper.selectById(1L)).thenReturn(savedArticle);

        boolean isOwner = articleService.isArticleOwner(1L, 2L);

        assertFalse(isOwner);
        verify(articleMapper, times(1)).selectById(1L);
    }

    @Test
    void isArticleOwner_ArticleNotFound() {
        when(articleMapper.selectById(1L)).thenReturn(null);

        boolean isOwner = articleService.isArticleOwner(1L, 1L);

        assertFalse(isOwner);
        verify(articleMapper, times(1)).selectById(1L);
    }
}