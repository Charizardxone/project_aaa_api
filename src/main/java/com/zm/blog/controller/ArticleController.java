package com.zm.blog.controller;

import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.dto.CommonResponse;
import com.zm.blog.service.ArticleService;
import com.zm.blog.service.IdempotencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    public ResponseEntity<CommonResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        try {
            // Get user ID from authenticated user
            Long authorId = getUserIdFromUserDetails(userDetails);
            log.info("User {} attempting to create article", authorId);

            // Handle idempotency
            if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
                if (idempotencyService.isDuplicateRequest(idempotencyKey)) {
                    log.warn("Duplicate request detected for user {} with key: {}", authorId, idempotencyKey);
                    return ResponseEntity.status(409)
                            .body(CommonResponse.error(409, "Duplicate request"));
                }
            }

            // Create article
            ArticleResponse response = articleService.createArticle(request, authorId);
            log.info("Article created successfully by user {} with ID: {}", authorId, response.getId());

            return ResponseEntity.ok(CommonResponse.success(response));

        } catch (IllegalArgumentException e) {
            log.error("Validation error while creating article: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CommonResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating article: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.error(500, "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ArticleResponse>> getArticle(@PathVariable Long id) {
        try {
            ArticleResponse response = articleService.getArticleById(id);
            return ResponseEntity.ok(CommonResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<ArticleResponse>> editArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleEditRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // Get user ID from authenticated user
            Long authorId = getUserIdFromUserDetails(userDetails);
            log.info("User {} attempting to edit article {}", authorId, id);

            // Edit article
            ArticleResponse response = articleService.editArticle(request, id, authorId);
            log.info("Article {} edited successfully by user {}", id, authorId);

            return ResponseEntity.ok(CommonResponse.success(response));

        } catch (IllegalArgumentException e) {
            log.error("Validation error while editing article: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CommonResponse.error(400, e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Concurrent modification error while editing article: {}", e.getMessage());
            return ResponseEntity.status(409)
                    .body(CommonResponse.error(409, e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error editing article: {}", e.getMessage(), e);
            if (e.getMessage().contains("Article not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.error(500, "Internal server error"));
        }
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        // Extract user ID from username or from token claims
        // This is a simplified implementation - in real scenario, you might extract from JWT claims
        String username = userDetails.getUsername();

        // For demo purposes, parse username as user ID
        // In production, you would have a proper user service
        try {
            return Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user format");
        }
    }
}