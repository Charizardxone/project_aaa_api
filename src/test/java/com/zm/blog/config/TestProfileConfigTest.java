package com.zm.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试环境配置验证测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class TestProfileConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void testProfileActivation() {
        assertEquals("test", environment.getActiveProfiles()[0]);
    }

    @Test
    void testServerConfig() {
        assertEquals("8081", environment.getProperty("server.port"));
        assertEquals("/api", environment.getProperty("server.servlet.context-path"));
    }

    @Test
    void testDatabaseConfig() {
        // 测试环境应该使用不同的数据库名
        String url = environment.getProperty("spring.datasource.url");
        assertNotNull(url);
        assertTrue(url.contains("blog_test"));
    }

    @Test
    void testJpaConfig() {
        assertEquals("validate", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("false", environment.getProperty("spring.jpa.show-sql"));
    }

    @Test
    void testLoggingConfig() {
        assertEquals("WARN", environment.getProperty("logging.level.root"));
        assertEquals("INFO", environment.getProperty("logging.level.com.zm.blog"));
        assertEquals("logs/blog-test.log", environment.getProperty("logging.file.name"));
    }

    @Test
    void testSwaggerConfig() {
        assertEquals("false", environment.getProperty("springdoc.api-docs.enabled"));
        assertEquals("false", environment.getProperty("springdoc.swagger-ui.enabled"));
    }

    @Test
    void testRedisConfig() {
        assertEquals("1", environment.getProperty("spring.redis.database"));
    }

    @Test
    void testCacheConfig() {
        assertEquals("redis", environment.getProperty("spring.cache.type"));
    }

    @Test
    void testJwtConfig() {
        assertNotNull(environment.getProperty("jwt.secret"));
        assertEquals("3600000", environment.getProperty("jwt.expiration"));
    }

    @Test
    void testManagementConfig() {
        String exposure = environment.getProperty("management.endpoints.web.exposure.include");
        assertNotNull(exposure);
        assertTrue(exposure.contains("prometheus"));
    }
}