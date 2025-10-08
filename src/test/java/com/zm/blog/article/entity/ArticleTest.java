package com.zm.blog.article.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    void sampleShouldCreateValidArticle() {
        Article article = Article.sample();

        assertEquals(1L, article.getId());
        assertEquals("Spring Boot 微服务架构最佳实践", article.getTitle());
        assertEquals("在构建现代微服务应用时，Spring Boot 提供了强大的框架支持。通过合理的架构设计，我们可以实现高可用、可扩展的分布式系统。本文将深入探讨如何利用 Spring Boot 的特性来构建生产级的微服务应用，包括服务发现、配置管理、负载均衡等关键技术的实现方案。", article.getContent());
        assertEquals(100L, article.getAuthorId());
        assertNotNull(article.getCreatedAt());
    }
}