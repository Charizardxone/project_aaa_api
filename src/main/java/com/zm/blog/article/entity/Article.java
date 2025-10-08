package com.zm.blog.article.entity;

import lombok.Data;
import java.time.Instant;

@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Instant createdAt;

    public static Article sample() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Sample Title");
        article.setContent("Sample Content");
        article.setAuthorId(100L);
        article.setCreatedAt(Instant.now());
        return article;
    }
}