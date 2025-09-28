package com.zm.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应用配置验证测试
 * 验证不同环境配置的正确性
 */
@SpringBootTest
@ActiveProfiles("dev")
public class ApplicationConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void testDevProfileConfig() {
        // 验证应用名称
        assertEquals("blog-article-service", environment.getProperty("spring.application.name"));

        // 验证开发环境端口
        assertEquals("8080", environment.getProperty("server.port"));

        // 验证上下文路径
        assertEquals("/api", environment.getProperty("server.servlet.context-path"));

        // 验证数据库配置
        assertNotNull(environment.getProperty("spring.datasource.url"));
        assertNotNull(environment.getProperty("spring.datasource.username"));
        assertNotNull(environment.getProperty("spring.datasource.password"));

        // 验证JPA配置
        assertEquals("update", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("true", environment.getProperty("spring.jpa.show-sql"));

        // 验证日志级别
        assertEquals("DEBUG", environment.getProperty("logging.level.com.zm.blog"));

        // 验证开发工具配置
        assertEquals("true", environment.getProperty("spring.devtools.restart.enabled"));

        // 验证跨域配置
        assertNotNull(environment.getProperty("app.cors.allowed-origins"));
    }

    @Test
    void testAppVersionConfig() {
        assertEquals("0.1.0", environment.getProperty("app.version"));
        assertEquals("博客系统核心文章管理模块", environment.getProperty("app.title"));
        assertEquals("DPAP Team", environment.getProperty("app.author"));
    }

    @Test
    void testFileUploadConfig() {
        assertEquals("/tmp/blog-uploads/", environment.getProperty("app.file.upload-path"));
        assertEquals("10485760", environment.getProperty("app.file.max-size"));
    }

    @Test
    void testJwtConfig() {
        assertNotNull(environment.getProperty("jwt.secret"));
        assertEquals("86400000", environment.getProperty("jwt.expiration"));
    }

    @Test
    void testMyBatisPlusConfig() {
        assertEquals("true", environment.getProperty("mybatis-plus.configuration.map-underscore-to-camel-case"));
        assertEquals("true", environment.getProperty("mybatis-plus.configuration.cache-enabled"));
    }

    @Test
    void testLoggingConfig() {
        assertEquals("INFO", environment.getProperty("logging.level.root"));
        assertEquals("logs/blog-dev.log", environment.getProperty("logging.file.name"));
    }

    @Test
    void testRedisConfig() {
        assertNotNull(environment.getProperty("spring.redis.host"));
        assertEquals("6379", environment.getProperty("spring.redis.port"));
        assertEquals("0", environment.getProperty("spring.redis.database"));
    }

    @Test
    void testManagementConfig() {
        assertNotNull(environment.getProperty("management.endpoints.web.exposure.include"));
        assertEquals("8081", environment.getProperty("management.server.port"));
    }
}