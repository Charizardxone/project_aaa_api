package com.zm.blog.controller;

import com.zm.blog.dto.ApiResponse;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.service.ArticleService;
import com.zm.blog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Article REST Controller
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    /**
     * Create new article
     * POST /api/articles
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        log.info("Received article creation request: {}", request.getTitle());

        // Validate request parameters
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage())
                    .orElse("参数验证失败");

            log.warn("Article creation validation failed: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, errorMessage));
        }

        try {
            // Extract and validate JWT token
            String token = extractToken(httpRequest);
            if (StringUtils.isBlank(token)) {
                log.warn("Missing authorization token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "未认证"));
            }

            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Token无效或已过期"));
            }

            // Get user ID from token
            Long authorId = jwtUtil.getUserIdFromToken(token);
            if (authorId == null) {
                log.warn("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "无效的用户信息"));
            }

            // Create article
            ArticleResponse response = articleService.createArticle(request, authorId);

            log.info("Article created successfully with ID: {}", response.getId());
            return ResponseEntity.ok(ApiResponse.success("文章创建成功", response));

        } catch (RuntimeException e) {
            log.error("Failed to create article: {}", e.getMessage());

            if (e.getMessage().contains("重复提交")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, e.getMessage()));
            }

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating article", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * Get article by ID
     * GET /api/articles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(@PathVariable Long id) {
        log.info("Fetching article with ID: {}", id);

        try {
            ArticleResponse response = articleService.getArticleById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            log.error("Failed to fetch article: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error fetching article", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * Edit existing article
     * PUT /api/articles/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> editArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleEditRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        log.info("Received article edit request for ID: {}", id);

        // Validate request parameters
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage())
                    .orElse("参数验证失败");

            log.warn("Article edit validation failed: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, errorMessage));
        }

        try {
            // Extract and validate JWT token
            String token = extractToken(httpRequest);
            if (StringUtils.isBlank(token)) {
                log.warn("Missing authorization token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "未认证"));
            }

            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Token无效或已过期"));
            }

            // Get user ID from token
            Long editorId = jwtUtil.getUserIdFromToken(token);
            if (editorId == null) {
                log.warn("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "无效的用户信息"));
            }

            // Edit article
            ArticleResponse response = articleService.editArticle(id, request, editorId);

            log.info("Article {} edited successfully", id);
            return ResponseEntity.ok(ApiResponse.success("文章编辑成功", response));

        } catch (RuntimeException e) {
            log.error("Failed to edit article: {}", e.getMessage());

            // Handle specific error types
            if (e.getMessage().contains("文章不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, e.getMessage()));
            } else if (e.getMessage().contains("无权限")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            } else if (e.getMessage().contains("被其他用户修改")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, e.getMessage()));
            }

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error editing article", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "服务器内部错误"));
        }
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}