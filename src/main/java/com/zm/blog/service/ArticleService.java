package com.zm.blog.service;

import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.entity.User;
import com.zm.blog.repository.ArticleRepository;
import com.zm.blog.repository.UserRepository;
import com.zm.blog.util.XssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Article service for business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // Simple in-memory cache for idempotency (in production, use Redis)
    private final ConcurrentHashMap<String, Long> idempotencyCache = new ConcurrentHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    public ArticleResponse createArticle(ArticleCreateRequest request, Long authorId) {
        log.info("Creating article for author: {}", authorId);

        // Generate idempotency key based on content hash
        String idempotencyKey = generateIdempotencyKey(request, authorId);

        // Check for duplicate submission
        Long existingArticleId = idempotencyCache.get(idempotencyKey);
        if (existingArticleId != null) {
            log.warn("Duplicate article creation attempt detected for key: {}", idempotencyKey);
            throw new RuntimeException("重复提交，请勿重复创建相同文章");
        }

        // Verify author exists
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("作者不存在"));

        // Create article entity
        Article article = new Article();
        article.setTitle(XssUtil.sanitizeText(request.getTitle()));
        article.setContent(XssUtil.sanitizeContent(request.getContent()));
        article.setSummary(XssUtil.sanitizeText(request.getSummary()));
        article.setTags(XssUtil.sanitizeText(request.getTags()));
        article.setStatus("draft");
        article.setAuthorId(authorId);

        // Save article
        Article savedArticle = articleRepository.save(article);

        // Cache the result for idempotency
        idempotencyCache.put(idempotencyKey, savedArticle.getId());

        log.info("Article created successfully with ID: {}", savedArticle.getId());

        // Build response
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(savedArticle, response);
        response.setAuthorName(author.getUsername());

        return response;
    }

    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findArticleWithAuthor(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(article, response);
        if (article.getAuthor() != null) {
            response.setAuthorName(article.getAuthor().getUsername());
        }
        return response;
    }

    private String generateIdempotencyKey(ArticleCreateRequest request, Long authorId) {
        return String.valueOf((request.getTitle() + request.getContent() + authorId).hashCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public ArticleResponse editArticle(Long articleId, ArticleEditRequest request, Long editorId) {
        log.info("Editing article {} by user: {}", articleId, editorId);

        // Find the article with author info
        Article article = articleRepository.findArticleWithAuthor(articleId);
        if (article == null) {
            log.warn("Article not found with ID: {}", articleId);
            throw new RuntimeException("文章不存在");
        }

        // Check if the editor is the author
        if (!article.getAuthorId().equals(editorId)) {
            log.warn("User {} attempted to edit article {} owned by {}", editorId, articleId, article.getAuthorId());
            throw new RuntimeException("无权限编辑此文章");
        }

        // Optimistic locking check
        if (!article.getUpdatedAt().equals(request.getUpdatedAt())) {
            log.warn("Optimistic lock failure - article {} was modified by another user", articleId);
            throw new RuntimeException("文章已被其他用户修改，请刷新后重试");
        }

        // Update article fields
        article.setTitle(XssUtil.sanitizeText(request.getTitle()));
        article.setContent(XssUtil.sanitizeContent(request.getContent()));
        article.setSummary(XssUtil.sanitizeText(request.getSummary()));
        article.setTags(XssUtil.sanitizeText(request.getTags()));
        // updatedAt will be automatically updated by @UpdateTimestamp

        // Save the updated article
        Article savedArticle = articleRepository.save(article);

        log.info("Article {} updated successfully", articleId);

        // Build response
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(savedArticle, response);
        if (savedArticle.getAuthor() != null) {
            response.setAuthorName(savedArticle.getAuthor().getUsername());
        }

        return response;
    }

    // Clean up old idempotency cache entries periodically (in production, handle this differently)
    public void cleanupIdempotencyCache() {
        if (idempotencyCache.size() > 1000) {
            idempotencyCache.clear();
        }
    }
}