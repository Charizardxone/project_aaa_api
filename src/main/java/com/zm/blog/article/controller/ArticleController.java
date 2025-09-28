package com.zm.blog.article.controller;

import com.zm.blog.article.dto.ArticleCreateRequest;
import com.zm.blog.article.dto.ArticleResponse;
import com.zm.blog.article.service.ArticleService;
import com.zm.blog.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/articles")
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {

        try {
            String username = authentication.getName();
            log.info("Received request to create article by user: {}", username);

            // Extract user information from JWT token
            Long authorId = extractUserIdFromAuthentication(authentication);
            String authorName = username;

            // Generate idempotency key if not provided
            if (idempotencyKey == null || idempotencyKey.isEmpty()) {
                idempotencyKey = UUID.randomUUID().toString();
            }

            ArticleResponse response = articleService.createArticle(request, authorId, authorName, idempotencyKey);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response));

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating article: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }

    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // In a real implementation, this would extract the user ID from the JWT claims
        // For now, we'll use a simple mapping from username to ID
        // This should be implemented based on your actual JWT token structure
        return 1L; // Default user ID for demo purposes
    }
}