package com.zm.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.dto.CommonResponse;
import com.zm.blog.service.ArticleService;
import com.zm.blog.service.IdempotencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private ArticleController articleController;

    private ObjectMapper objectMapper;
    private UserDetails userDetails;
    private ArticleCreateRequest validRequest;
    private ArticleResponse articleResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userDetails = new User("1", "password", Collections.emptyList());

        validRequest = new ArticleCreateRequest(
            "Test Title",
            "Test Content",
            "Test Summary",
            "test, tags"
        );

        articleResponse = new ArticleResponse();
        articleResponse.setId(1L);
        articleResponse.setTitle("Test Title");
        articleResponse.setContent("Test Content");
        articleResponse.setSummary("Test Summary");
        articleResponse.setTags("test, tags");
        articleResponse.setStatus("draft");
        articleResponse.setAuthorId(1L);
    }

    @Test
    void createArticle_Success() {
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
            .thenReturn(articleResponse);

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.createArticle(validRequest, userDetails, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCode());
        assertEquals("success", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals("Test Title", response.getBody().getData().getTitle());

        verify(articleService, times(1)).createArticle(any(ArticleCreateRequest.class), any(Long.class));
    }

    @Test
    void createArticle_WithIdempotencyKey_FirstRequest() {
        when(idempotencyService.isDuplicateRequest("test-key-123")).thenReturn(false);
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
            .thenReturn(articleResponse);

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.createArticle(validRequest, userDetails, "test-key-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCode());

        verify(idempotencyService, times(1)).isDuplicateRequest("test-key-123");
        verify(articleService, times(1)).createArticle(any(ArticleCreateRequest.class), any(Long.class));
    }

    @Test
    void createArticle_WithIdempotencyKey_DuplicateRequest() {
        when(idempotencyService.isDuplicateRequest("test-key-123")).thenReturn(true);

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.createArticle(validRequest, userDetails, "test-key-123");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getCode());
        assertEquals("Duplicate request", response.getBody().getMessage());

        verify(idempotencyService, times(1)).isDuplicateRequest("test-key-123");
        verify(articleService, never()).createArticle(any(ArticleCreateRequest.class), any(Long.class));
    }

    @Test
    void createArticle_ValidationException() {
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
            .thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.createArticle(validRequest, userDetails, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertEquals("Invalid input", response.getBody().getMessage());
    }

    @Test
    void createArticle_RuntimeException() {
        when(articleService.createArticle(any(ArticleCreateRequest.class), any(Long.class)))
            .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.createArticle(validRequest, userDetails, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("Internal server error", response.getBody().getMessage());
    }

    @Test
    void getArticle_Success() {
        when(articleService.getArticleById(1L)).thenReturn(articleResponse);

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.getArticle(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCode());
        assertEquals("success", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(1L, response.getBody().getData().getId());

        verify(articleService, times(1)).getArticleById(1L);
    }

    @Test
    void getArticle_NotFound() {
        when(articleService.getArticleById(999L))
            .thenThrow(new RuntimeException("Article not found"));

        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.getArticle(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(articleService, times(1)).getArticleById(999L);
    }

    @Test
    void getUserIdFromUserDetails_ValidNumeric() {
        UserDetails user = new User("123", "password", Collections.emptyList());
        when(articleService.createArticle(any(ArticleCreateRequest.class), eq(123L)))
            .thenReturn(articleResponse);

        // This should work without throwing an exception
        ResponseEntity<CommonResponse<ArticleResponse>> response =
            articleController.createArticle(validRequest, user, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCode());
    }
}