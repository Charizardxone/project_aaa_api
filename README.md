# 博客系统核心文章管理模块 - Blog Article Service

## 项目概述

本项目是基于 Spring Boot 3.x 构建的博客系统核心文章管理模块，提供统一、可扩展、分环境的配置体系，支持后续 API 功能（文章创建/编辑/发布）的开发。

## 技术栈

- **后端框架**: Spring Boot 3.5.5
- **数据库**: MySQL (生产环境) / H2 (开发/测试环境)
- **ORM框架**: MyBatis-Plus 3.5.7
- **安全框架**: Spring Security
- **API文档**: Swagger/OpenAPI 3
- **缓存**: Redis (生产环境) / Simple (开发/测试环境)
- **构建工具**: Maven 3
- **开发语言**: Java 17

## 环境配置

项目支持多环境配置，通过 Spring Profiles 进行环境切换：

### 1. 开发环境 (dev)

**启动方式**:
```bash
# 使用默认dev配置启动
mvn spring-boot:run

# 或者明确指定dev profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

**配置特点**:
- 端口: 8080
- 数据库: H2 内存数据库
- H2 控制台: http://localhost:8080/h2-console
- 日志级别: DEBUG
- Swagger文档: 启用
- 跨域: 允许本地开发

### 2. 测试环境 (test)

**启动方式**:
```bash
mvn spring-boot:run -Dspring.profiles.active=test
```

**配置特点**:
- 端口: 8081
- 数据库: H2 内存数据库
- 日志级别: WARN
- Swagger文档: 启用
- 跨域: 限制测试域名

### 3. 生产环境 (prod)

**启动方式**:
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

**配置特点**:
- 端口: 8082
- 数据库: MySQL
- 日志级别: WARN
- Swagger文档: 默认关闭
- 跨域: 严格限制
- SSL: 支持配置

## 环境变量配置

生产环境支持通过环境变量进行配置：

```bash
# 数据库配置
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=blog_db
export DB_USERNAME=root
export DB_PASSWORD=your_password

# JWT配置
export JWT_SECRET=your_jwt_secret_key
export JWT_EXPIRATION=86400

# Redis配置
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=your_redis_password

# SSL配置
export SSL_ENABLED=true
export SSL_KEY_STORE=/path/to/keystore.p12
export SSL_KEY_STORE_PASSWORD=your_password
```

## 配置文件结构

```
src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境配置
├── application-test.yml         # 测试环境配置
└── application-prod.yml         # 生产环境配置
```

## 启动和部署

### 本地开发

1. **安装依赖**
```bash
mvn clean install
```

2. **启动应用**
```bash
# 开发环境
mvn spring-boot:run

# 测试环境
mvn spring-boot:run -Dspring.profiles.active=test

# 生产环境
mvn spring-boot:run -Dspring.profiles.active=prod
```

3. **访问应用**
- 开发环境: http://localhost:8080
- 测试环境: http://localhost:8081
- 生产环境: http://localhost:8082

### 生产部署

1. **构建 JAR 包**
```bash
mvn clean package
```

2. **运行 JAR 包**
```bash
# 使用默认配置
java -jar target/blog-0.0.1-SNAPSHOT.jar

# 指定环境
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 设置环境变量
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod \
  --DB_HOST=production-db \
  --DB_PASSWORD=secure_password
```

## API 文档

- **Swagger UI**: http://localhost:8080/swagger-ui.html (开发/测试环境)
- **API 文档 JSON**: http://localhost:8080/v3/api-docs

## 健康检查

应用提供 Spring Boot Actuator 健康检查端点：

- **健康检查**: http://localhost:8080/actuator/health
- **应用信息**: http://localhost:8080/actuator/info
- **指标监控**: http://localhost:8080/actuator/metrics

## 测试

运行所有测试：
```bash
mvn test
```

运行特定测试：
```bash
mvn test -Dtest=BlogApplicationTests
```

## 安全说明

- 所有敏感信息使用环境变量配置
- 生产环境禁止在配置文件中硬编码密码
- JWT 密钥需要在生产环境中更改为强密码
- 建议使用 HTTPS 和 SSL 证书

## 开发指南

### 代码规范

- 使用 Lombok 减少模板代码
- 遵循 Spring Boot 最佳实践
- 使用 MyBatis-Plus 进行数据库操作
- 集成 Swagger 进行 API 文档管理

### 环境变量优先级

1. 命令行参数 (最高优先级)
2. 环境变量
3. 配置文件
4. 默认值 (最低优先级)

## 许可证

本项目采用 MIT 许可证。
