package com.zm.blog.service;

import com.zm.blog.dto.ArticleCreateRequest;
import com.zm.blog.dto.ArticleResponse;

public interface ArticleService {

    ArticleResponse createArticle(ArticleCreateRequest request, Long authorId);
}