package com.zm.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.blog.common.XssUtils;
import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;
import com.zm.blog.entity.Article;
import com.zm.blog.entity.User;
import com.zm.blog.mapper.ArticleMapper;
import com.zm.blog.mapper.UserMapper;
import com.zm.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request, Long authorId) {
        log.info("Creating article for authorId: {}", authorId);

        // Validate user exists
        User user = userMapper.selectById(authorId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // XSS filtering
        String title = XssUtils.clean(request.getTitle());
        String content = XssUtils.clean(request.getContent());
        String summary = request.getSummary() != null ? XssUtils.clean(request.getSummary()) : null;
        String tags = request.getTags() != null ? XssUtils.clean(request.getTags()) : null;

        // Check for duplicate articles (idempotency)
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getTitle, title)
                   .eq(Article::getAuthorId, authorId)
                   .eq(Article::getStatus, "DRAFT")
                   .orderByDesc(Article::getCreatedAt)
                   .last("LIMIT 1");

        Article existingArticle = articleMapper.selectOne(queryWrapper);
        if (existingArticle != null &&
            existingArticle.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            throw new RuntimeException("文章创建过于频繁，请稍后再试");
        }

        // Create article
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setSummary(summary);
        article.setTags(tags);
        article.setStatus("DRAFT");
        article.setAuthorId(authorId);
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setCommentCount(0L);
        article.setDeleted(0);
        article.setVersion(1);

        int result = articleMapper.insert(article);
        if (result <= 0) {
            throw new RuntimeException("文章创建失败");
        }

        // Convert to response
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(article, response);
        response.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());

        log.info("Article created successfully with id: {}", article.getId());
        return response;
    }
}