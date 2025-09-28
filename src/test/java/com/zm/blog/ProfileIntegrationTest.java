package com.zm.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Profile集成测试 - 验证不同环境配置的正确加载
 */
@SpringBootTest
public class ProfileIntegrationTest {

    @Autowired
    private Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    @Value("${app.version}")
    private String appVersion;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Test
    void testDevProfileConfiguration() {
        // 验证开发环境配置
        assertEquals("blog-article-service", applicationName);
        assertEquals("0.1.0", appVersion);

        // 验证当前激活的profile
        assertTrue(activeProfile.contains("dev") || activeProfile.contains("test"));

        // 验证开发环境端口
        assertTrue(serverPort.equals("8080") || serverPort.equals("8081"));

        // 验证H2数据库配置
        assertEquals("org.h2.Driver", environment.getProperty("spring.datasource.driver-class-name"));

        // 验证JPA配置
        assertEquals("true", environment.getProperty("spring.jpa.show-sql"));
    }

    @Test
    void testApplicationPropertiesLoaded() {
        // 验证主配置文件属性
        assertEquals("blog-article-service", environment.getProperty("spring.application.name"));
        assertEquals("0.1.0", environment.getProperty("app.version"));

        // 验证JWT配置
        assertTrue(environment.getProperty("app.jwt.secret") != null);
        assertTrue(environment.getProperty("app.jwt.expiration") != null);

        // 验证MyBatis-Plus配置
        assertEquals("true", environment.getProperty("mybatis-plus.configuration.map-underscore-to-camel-case"));
    }

    @Test
    void testSecurityConfiguration() {
        // 验证安全配置
        assertTrue(environment.getProperty("spring.security.user.name") == null ||
                  environment.getProperty("spring.security.user.name").isEmpty());
    }

    @Test
    void testDatabaseConfiguration() {
        // 验证数据库配置
        assertEquals("mem", environment.getProperty("spring.datasource.url").split(":")[2]);
        assertTrue(environment.getProperty("spring.datasource.username") != null);
        assertTrue(environment.getProperty("spring.datasource.password") != null);
    }
}