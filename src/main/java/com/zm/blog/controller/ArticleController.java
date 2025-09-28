package com.zm.blog.controller;

import com.zm.blog.dto.ApiResponse;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "文章管理", description = "文章创建、编辑、查询等相关接口")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章
     */
    @PostMapping
    @Operation(summary = "创建文章", description = "创建新文章，初始状态为草稿")
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request) {

        // 获取当前用户ID（简化实现，实际应从JWT中获取）
        Long currentUserId = getCurrentUserId();

        try {
            ArticleResponse response = articleService.createArticle(request, currentUserId);
            log.info("用户 {} 创建文章成功，文章ID: {}", currentUserId, response.getId());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 编辑文章
     */
    @PutMapping("/{id}")
    @Operation(summary = "编辑文章", description = "编辑指定文章，仅作者本人可编辑")
    public ResponseEntity<ApiResponse<ArticleResponse>> editArticle(
            @PathVariable @Parameter(description = "文章ID") Long id,
            @Valid @RequestBody ArticleEditRequest request) {

        // 获取当前用户ID
        Long currentUserId = getCurrentUserId();

        try {
            ArticleResponse response = articleService.editArticle(id, request, currentUserId);
            log.info("用户 {} 编辑文章成功，文章ID: {}", currentUserId, id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("编辑文章失败", e);
            if (e.getMessage().contains("404")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("403")) {
                return ResponseEntity.status(403).body(ApiResponse.error(403, "无权限编辑此文章"));
            } else if (e.getMessage().contains("409")) {
                return ResponseEntity.status(409).body(ApiResponse.error(409, "并发冲突，请刷新后重试"));
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情", description = "根据ID获取文章详情")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(
            @PathVariable @Parameter(description = "文章ID") Long id) {

        try {
            ArticleResponse response = articleService.getArticleById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("获取文章失败", e);
            if (e.getMessage().contains("404")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取当前用户ID（简化实现）
     */
    private Long getCurrentUserId() {
        // 在实际项目中，应该从JWT token中解析用户ID
        // 这里简化实现，返回固定用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 这里应该从JWT中解析用户ID，暂时返回1
            return 1L;
        }
        // 如果没有认证，返回默认用户ID（仅用于演示）
        return 1L;
    }
}