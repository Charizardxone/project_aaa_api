package com.zm.blog.article.controller;

import com.zm.blog.article.entity.Article;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ArticleSampleController {

    @GetMapping("/api/articles/sample")
    public Map<String, Object> sample() {
        Article article = Article.sample();
        return Map.of(
            "code", 0,
            "message", "success",
            "data", article
        );
    }
}