package com.zm.blog.article.entity;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    void testSampleArticle() {
        Article article = Article.sample();

        assertEquals(1L, article.getId());
        assertEquals("Sample Title", article.getTitle());
        assertEquals("Sample Content", article.getContent());
        assertEquals(100L, article.getAuthorId());
        assertEquals(Instant.parse("2025-09-29T12:00:00Z"), article.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Article article = new Article();

        article.setId(2L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setAuthorId(200L);
        article.setCreatedAt(Instant.now());

        assertEquals(2L, article.getId());
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Content", article.getContent());
        assertEquals(200L, article.getAuthorId());
        assertNotNull(article.getCreatedAt());
    }
}