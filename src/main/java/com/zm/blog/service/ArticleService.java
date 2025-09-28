package com.zm.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.mapper.ArticleMapper;
import com.zm.blog.util.SqlInjectionUtils;
import com.zm.blog.util.XssUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleMapper articleMapper;

    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, Long authorId) {
        log.info("Creating article for author ID: {}", authorId);

        // XSS filtering and SQL injection protection
        String cleanTitle = XssUtils.cleanForText(SqlInjectionUtils.sanitize(request.getTitle()));
        String cleanContent = XssUtils.clean(SqlInjectionUtils.sanitize(request.getContent()));
        String cleanSummary = request.getSummary() != null ?
            XssUtils.cleanForText(SqlInjectionUtils.sanitize(request.getSummary())) : null;
        String cleanTags = request.getTags() != null ?
            XssUtils.cleanForText(SqlInjectionUtils.sanitize(request.getTags())) : null;

        // Create article entity
        Article article = new Article();
        article.setTitle(cleanTitle);
        article.setContent(cleanContent);
        article.setSummary(cleanSummary);
        article.setTags(cleanTags);
        article.setStatus("draft");
        article.setAuthorId(authorId);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        // Save article
        int result = articleMapper.insert(article);
        if (result != 1) {
            throw new RuntimeException("Failed to create article");
        }

        log.info("Article created successfully with ID: {}", article.getId());

        // Return response
        return convertToResponse(article);
    }

    public ArticleResponse getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("Article not found");
        }
        return convertToResponse(article);
    }

    public boolean existsById(Long id) {
        return articleMapper.selectById(id) != null;
    }

    public boolean isArticleOwner(Long articleId, Long userId) {
        Article article = articleMapper.selectById(articleId);
        return article != null && article.getAuthorId().equals(userId);
    }

    @Transactional
    public ArticleResponse editArticle(ArticleEditRequest request, Long articleId, Long userId) {
        log.info("Editing article {} by user {}", articleId, userId);

        // Check if article exists and user is the owner
        Article existingArticle = articleMapper.selectById(articleId);
        if (existingArticle == null) {
            throw new RuntimeException("Article not found");
        }

        if (!existingArticle.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own articles");
        }

        // Optimistic locking check
        if (!existingArticle.getUpdatedAt().equals(request.getUpdatedAt())) {
            throw new IllegalStateException("Concurrent modification detected. Please refresh and try again.");
        }

        // XSS filtering and SQL injection protection
        String cleanTitle = XssUtils.cleanForText(SqlInjectionUtils.sanitize(request.getTitle()));
        String cleanContent = XssUtils.clean(SqlInjectionUtils.sanitize(request.getContent()));
        String cleanSummary = request.getSummary() != null ?
            XssUtils.cleanForText(SqlInjectionUtils.sanitize(request.getSummary())) : null;
        String cleanTags = request.getTags() != null ?
            XssUtils.cleanForText(SqlInjectionUtils.sanitize(request.getTags())) : null;

        // Update article
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleId)
                    .eq("updated_at", request.getUpdatedAt()) // Optimistic locking
                    .set("title", cleanTitle)
                    .set("content", cleanContent)
                    .set("summary", cleanSummary)
                    .set("tags", cleanTags)
                    .set("updated_at", LocalDateTime.now());

        int result = articleMapper.update(null, updateWrapper);
        if (result == 0) {
            throw new IllegalStateException("Concurrent modification detected. Please refresh and try again.");
        }

        log.info("Article {} updated successfully", articleId);

        // Return updated article
        return getArticleById(articleId);
    }

    private ArticleResponse convertToResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setContent(article.getContent());
        response.setSummary(article.getSummary());
        response.setTags(article.getTags());
        response.setStatus(article.getStatus());
        response.setAuthorId(article.getAuthorId());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());
        return response;
    }
}