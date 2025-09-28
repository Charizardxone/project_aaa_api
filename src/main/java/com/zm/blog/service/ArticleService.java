package com.zm.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.blog.common.BusinessException;
import com.zm.blog.common.SecurityUtils;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleEditRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文章服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleMapper articleMapper;

    /**
     * 创建文章
     */
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, Long authorId) {
        // 参数过滤
        String filteredTitle = SecurityUtils.contentFilter(request.getTitle());
        String filteredContent = SecurityUtils.contentFilter(request.getContent());
        String filteredSummary = request.getSummary() != null ? SecurityUtils.contentFilter(request.getSummary()) : null;
        String filteredTags = request.getTags() != null ? SecurityUtils.contentFilter(request.getTags()) : null;

        // 检查重复提交（简单实现）
        String fingerprint = SecurityUtils.generateRequestFingerprint(filteredTitle, filteredContent, authorId);

        // 创建文章实体
        Article article = new Article();
        article.setTitle(filteredTitle);
        article.setContent(filteredContent);
        article.setSummary(filteredSummary);
        article.setTags(filteredTags);
        article.setAuthorId(authorId);
        article.setStatus("draft");

        try {
            int result = articleMapper.insert(article);
            if (result <= 0) {
                throw new BusinessException("创建文章失败");
            }

            // 重新查询获取完整的文章信息（包括ID和时间戳）
            Article createdArticle = articleMapper.selectById(article.getId());

            log.info("创建文章成功，ID: {}, 作者ID: {}", createdArticle.getId(), authorId);
            return convertToResponse(createdArticle);

        } catch (Exception e) {
            log.error("创建文章失败", e);
            throw new BusinessException("创建文章失败: " + e.getMessage());
        }
    }

    /**
     * 编辑文章
     */
    @Transactional
    public ArticleResponse editArticle(Long id, ArticleEditRequest request, Long authorId) {
        // 查询文章
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        // 检查权限
        if (!article.getAuthorId().equals(authorId)) {
            throw new BusinessException(403, "无权限编辑此文章");
        }

        // 检查乐观锁
        if (!article.getUpdatedAt().equals(request.getUpdatedAt())) {
            throw new BusinessException(409, "文章已被其他用户修改，请刷新后重试");
        }

        // 参数过滤
        String filteredTitle = SecurityUtils.contentFilter(request.getTitle());
        String filteredContent = SecurityUtils.contentFilter(request.getContent());
        String filteredSummary = request.getSummary() != null ? SecurityUtils.contentFilter(request.getSummary()) : null;
        String filteredTags = request.getTags() != null ? SecurityUtils.contentFilter(request.getTags()) : null;

        // 更新文章
        article.setTitle(filteredTitle);
        article.setContent(filteredContent);
        article.setSummary(filteredSummary);
        article.setTags(filteredTags);
        article.setUpdatedAt(LocalDateTime.now());

        try {
            int result = articleMapper.updateById(article);
            if (result <= 0) {
                throw new BusinessException("更新文章失败");
            }

            log.info("更新文章成功，ID: {}, 作者ID: {}", id, authorId);
            return convertToResponse(articleMapper.selectWithAuthor(id));

        } catch (Exception e) {
            log.error("更新文章失败", e);
            throw new BusinessException("更新文章失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询文章
     */
    public ArticleResponse getArticleById(Long id) {
        Article article = articleMapper.selectWithAuthor(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        return convertToResponse(article);
    }

    /**
     * 转换为响应DTO
     */
    private ArticleResponse convertToResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(article, response);
        return response;
    }
}