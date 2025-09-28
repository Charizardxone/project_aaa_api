package com.zm.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 生产环境配置验证测试
 */
@SpringBootTest
@ActiveProfiles("prod")
@TestPropertySource(properties = {
        "DB_HOST=localhost",
        "DB_NAME=blog_prod",
        "DB_USERNAME=prod_user",
        "DB_PASSWORD=prod_password",
        "JWT_SECRET=test-jwt-secret",
        "MAIL_HOST=smtp.example.com",
        "MAIL_USERNAME=prod@example.com",
        "MAIL_PASSWORD=prod-password",
        "REDIS_HOST=localhost",
        "REDIS_PASSWORD=redis_password",
        "ACTUATOR_USERNAME=admin",
        "ACTUATOR_PASSWORD=admin123"
})
public class ProdProfileConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void testProfileActivation() {
        assertEquals("prod", environment.getActiveProfiles()[0]);
    }

    @Test
    void testServerConfig() {
        assertEquals("8080", environment.getProperty("server.port"));
        assertEquals("/api", environment.getProperty("server.servlet.context-path"));
    }

    @Test
    void testDatabaseConfig() {
        // 生产环境数据库配置应该使用环境变量
        String url = environment.getProperty("spring.datasource.url");
        assertNotNull(url);
        assertTrue(url.contains("useSSL=true"));
        assertTrue(url.contains("blog_prod"));

        // 验证连接池配置
        assertEquals("50", environment.getProperty("spring.datasource.hikari.maximum-pool-size"));
        assertEquals("20", environment.getProperty("spring.datasource.hikari.minimum-idle"));
    }

    @Test
    void testJpaConfig() {
        assertEquals("validate", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("false", environment.getProperty("spring.jpa.show-sql"));

        // 验证性能优化配置
        assertEquals("50", environment.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size"));
        assertEquals("true", environment.getProperty("spring.jpa.properties.hibernate.order_inserts"));
    }

    @Test
    void testLoggingConfig() {
        assertEquals("WARN", environment.getProperty("logging.level.root"));
        assertEquals("INFO", environment.getProperty("logging.level.com.zm.blog"));

        // 验证生产环境日志路径
        String logPath = environment.getProperty("logging.file.name");
        assertNotNull(logPath);
        assertTrue(logPath.contains("/var/log/blog/"));
    }

    @Test
    void testSecurityConfig() {
        // 验证Swagger是否禁用
        assertEquals("false", environment.getProperty("springdoc.api-docs.enabled"));
        assertEquals("false", environment.getProperty("springdoc.swagger-ui.enabled"));

        // 验证JWT配置
        assertNotNull(environment.getProperty("jwt.secret"));
        assertEquals("7200000", environment.getProperty("jwt.expiration"));
    }

    @Test
    void testManagementConfig() {
        // 验证管理端口
        assertEquals("8081", environment.getProperty("management.server.port"));
        assertEquals("127.0.0.1", environment.getProperty("management.server.address"));

        // 验证端点暴露
        String exposure = environment.getProperty("management.endpoints.web.exposure.include");
        assertNotNull(exposure);
        assertFalse(exposure.contains("*"));
    }

    @Test
    void testPerformanceConfig() {
        // 验证Tomcat线程池配置
        assertEquals("200", environment.getProperty("server.tomcat.max-threads"));
        assertEquals("20", environment.getProperty("server.tomcat.min-spare-threads"));

        // 验证Redis配置
        assertEquals("50", environment.getProperty("spring.redis.lettuce.pool.max-active"));
        assertEquals("2000ms", environment.getProperty("spring.redis.timeout"));
    }

    @Test
    void testCacheConfig() {
        assertEquals("redis", environment.getProperty("spring.cache.type"));
        assertEquals("blog:", environment.getProperty("spring.cache.redis.key-prefix"));
    }

    @Test
    void testFileUploadConfig() {
        // 验证生产环境文件上传路径
        String uploadPath = environment.getProperty("app.file.upload-path");
        assertNotNull(uploadPath);
        assertTrue(uploadPath.contains("/app/uploads/"));

        // 验证CDN配置
        assertNotNull(environment.getProperty("app.file.cdn-url"));
    }

    @Test
    void testMailConfig() {
        // 验证邮件配置使用环境变量
        assertNotNull(environment.getProperty("spring.mail.host"));
        assertNotNull(environment.getProperty("spring.mail.username"));
        assertNotNull(environment.getProperty("spring.mail.password"));
    }

    @Test
    void testTaskConfig() {
        // 验证任务调度配置
        assertEquals("10", environment.getProperty("spring.task.scheduling.pool.size"));
        assertEquals("50", environment.getProperty("spring.task.execution.pool.max-size"));
    }

    @Test
    void testHealthConfig() {
        // 验证健康检查配置
        assertEquals("never", environment.getProperty("management.endpoint.health.show-details"));
        assertEquals("true", environment.getProperty("management.endpoint.health.probes.enabled"));
    }

    @Test
    void testProductionSecurityFeatures() {
        // 验证生产环境安全特性
        assertEquals("false", environment.getProperty("spring.main.allow-bean-definition-overriding"));
        assertEquals("true", environment.getProperty("spring.jpa.hibernate.ddl-auto").equals("validate"));
        assertEquals("false", environment.getProperty("spring.jpa.show-sql"));
    }

    @Test
    void testContainerizationSupport() {
        // 验证容器化支持
        assertNotNull(environment.getProperty("SERVER_PORT"));
        assertNotNull(environment.getProperty("SERVER_CONTEXT_PATH"));
        assertNotNull(environment.getProperty("LOG_FILE_PATH"));
        assertNotNull(environment.getProperty("FILE_UPLOAD_PATH"));
        assertNotNull(environment.getProperty("CDN_URL"));
    }

    @Test
    void testMonitoringAndMetrics() {
        // 验证监控和指标配置
        assertEquals("true", environment.getProperty("management.metrics.export.prometheus.enabled"));
        assertEquals("production", environment.getProperty("management.metrics.tags.environment"));
        assertEquals("blog-article-service", environment.getProperty("management.metrics.tags.application"));
    }
}