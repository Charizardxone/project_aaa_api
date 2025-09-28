package com.zm.blog.controller;

import com.zm.blog.dto.ApiResponse;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "文章管理", description = "文章创建、编辑、发布等相关接口")
@SecurityRequirement(name = "bearer-key")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/articles")
    @Operation(summary = "创建文章", description = "创建新的文章，初始状态为草稿")
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request) {

        // TODO: Implement proper JWT authentication and extract user ID
        // For now, using a hardcoded user ID for testing
        Long authorId = getCurrentUserId();

        ArticleResponse response = articleService.createArticle(request, authorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get current user ID from authentication context
     * This is a simplified version - in production, you would extract the user ID from JWT claims
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // For testing purposes, return a hardcoded user ID
            // In production, extract from JWT claims
            return 1L;
        }
        throw new RuntimeException("用户未认证");
    }
}