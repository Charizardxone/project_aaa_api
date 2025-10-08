package com.zm.blog.article.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    void sampleShouldCreateValidArticle() {
        Article article = Article.sample();

        assertEquals(1L, article.getId());
        assertEquals("Sample Title", article.getTitle());
        assertEquals("Sample Content", article.getContent());
        assertEquals(100L, article.getAuthorId());
        assertNotNull(article.getCreatedAt());
    }
}