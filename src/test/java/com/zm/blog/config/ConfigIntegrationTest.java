package com.zm.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置集成测试
 * 验证配置文件加载和属性覆盖机制
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.profiles.active=dev",
        "custom.override.property=overridden"
})
public class ConfigIntegrationTest {

    @Autowired
    private Environment environment;

    @Test
    void testProfileHierarchy() {
        // 验证主配置文件加载
        assertEquals("blog-article-service", environment.getProperty("spring.application.name"));
        assertEquals("0.1.0", environment.getProperty("app.version"));

        // 验证dev profile配置覆盖
        assertEquals("8080", environment.getProperty("server.port"));
        assertEquals("/api", environment.getProperty("server.servlet.context-path"));
    }

    @Test
    void testEnvironmentVariableOverride() {
        // 验证环境变量覆盖机制
        // 通过-Dspring.profiles.active=test 参数设置
        String activeProfile = System.getProperty("spring.profiles.active");
        if (activeProfile != null) {
            assertEquals(activeProfile, environment.getActiveProfiles()[0]);
        }
    }

    @Test
    void testCustomPropertyOverride() {
        // 验证测试属性覆盖
        assertEquals("overridden", environment.getProperty("custom.override.property"));
    }

    @Test
    void testPropertyPlaceholderResolution() {
        // 验证占位符解析
        String dbHost = environment.getProperty("DB_HOST");
        if (dbHost != null) {
            assertTrue(environment.getProperty("spring.datasource.url").contains(dbHost));
        } else {
            // 使用默认值
            assertTrue(environment.getProperty("spring.datasource.url").contains("localhost"));
        }
    }

    @Test
    void testMultipleProfiles() {
        // 验证多profile支持
        String[] activeProfiles = environment.getActiveProfiles();
        assertTrue(activeProfiles.length > 0);

        // 验证配置合并
        assertNotNull(environment.getProperty("spring.datasource.url"));
        assertNotNull(environment.getProperty("logging.level.root"));
        assertNotNull(environment.getProperty("app.version"));
    }

    @Test
    void testConfigurationSanity() {
        // 验证配置完整性
        assertNotNull(environment.getProperty("spring.application.name"));
        assertNotNull(environment.getProperty("server.port"));
        assertNotNull(environment.getProperty("app.version"));
        assertNotNull(environment.getProperty("logging.level.root"));

        // 验证关键配置不为空
        assertFalse(environment.getProperty("spring.datasource.url").isEmpty());
        assertFalse(environment.getProperty("jwt.secret").isEmpty());
    }

    @Test
    void testProductionSecurity() {
        // 验证生产环境安全配置
        if (environment.getActiveProfiles()[0].equals("prod")) {
            // 验证敏感信息不包含默认值
            String jwtSecret = environment.getProperty("jwt.secret");
            assertFalse(jwtSecret.contains("dev-jwt-secret"));
            assertFalse(jwtSecret.contains("test123456"));

            // 验证Swagger禁用
            assertEquals("false", environment.getProperty("springdoc.api-docs.enabled"));
        }
    }

    @Test
    void testDevelopmentFeatures() {
        // 验证开发环境特性
        if (environment.getActiveProfiles()[0].equals("dev")) {
            // 验证开发工具启用
            assertEquals("true", environment.getProperty("spring.devtools.restart.enabled"));

            // 验证详细日志
            assertEquals("DEBUG", environment.getProperty("logging.level.com.zm.blog"));

            // 验证SQL显示
            assertEquals("true", environment.getProperty("spring.jpa.show-sql"));
        }
    }

    @Test
    void testTestEnvironmentFeatures() {
        // 验证测试环境特性
        if (environment.getActiveProfiles()[0].equals("test")) {
            // 验证测试端口
            assertEquals("8081", environment.getProperty("server.port"));

            // 验证Swagger禁用
            assertEquals("false", environment.getProperty("springdoc.api-docs.enabled"));

            // 验证Prometheus启用
            String exposure = environment.getProperty("management.endpoints.web.exposure.include");
            assertTrue(exposure.contains("prometheus"));
        }
    }
}