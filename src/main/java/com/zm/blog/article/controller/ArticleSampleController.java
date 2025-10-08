package com.zm.blog.article.controller;

import com.zm.blog.article.entity.Article;
import com.zm.blog.dto.CommonResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleSampleController {

    @GetMapping("/api/articles/sample")
    public CommonResp<Article> sample() {
        Article article = Article.sample();
        return CommonResp.success(article);
    }
}