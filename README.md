# 博客系统核心文章管理模块 API

## 项目概述

博客系统核心文章管理模块，提供文章创建、编辑、发布等核心功能的微服务。基于 Spring Boot 3.x 构建，采用分环境配置管理策略。

## 技术栈

- **框架**: Spring Boot 3.5.5
- **Java 版本**: JDK 17
- **构建工具**: Maven
- **数据库**: MySQL (支持 MyBatis-Plus 和 JPA)
- **配置格式**: YAML

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/zm/blog/
│   │       └── BlogApplication.java
│   └── resources/
│       ├── application.yml           # 主配置文件
│       ├── application-dev.yml       # 开发环境配置
│       ├── application-test.yml      # 测试环境配置
│       └── application-prod.yml      # 生产环境配置
└── test/
    └── java/
        └── com/zm/blog/
            └── BlogApplicationTests.java
```

## 配置系统

### 配置架构设计

本项目采用分层配置策略：

1. **主配置文件** (`application.yml`): 包含通用配置和占位符
2. **环境配置文件** (`application-{profile}.yml`): 覆盖特定环境的差异化配置
3. **外部化配置**: 支持环境变量和外部配置注入

### 配置命名规范

- `app.*`: 应用基础信息配置
- `feature.*`: 功能模块配置
- `infra.*`: 基础设施配置
- `jwt.*`: JWT 认证相关配置
- `cors.*`: 跨域相关配置

### 环境配置

#### 开发环境 (dev)
- 端口: 8080
- 数据库: 本地 MySQL (blog_dev)
- 日志级别: DEBUG
- 功能: 全部调试功能开启

#### 测试环境 (test)
- 端口: 8081
- 数据库: 测试服务器 MySQL (blog_test)
- 日志级别: INFO
- 功能: 部分调试功能开启

#### 生产环境 (prod)
- 端口: 配置化 (默认8080)
- 数据库: 外部化配置
- 日志级别: WARN
- 功能: 调试功能关闭，性能优化

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 5.7+ (开发和测试需要)

### 启动应用

#### 开发环境启动
```bash
# 使用默认 dev 配置启动
mvn spring-boot:run

# 或显式指定 dev 配置
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 测试环境启动
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

#### 生产环境启动
```bash
# 需要设置必要的环境变量
export DB_URL="jdbc:mysql://prod-db:3306/blog_prod?useSSL=true"
export DB_USERNAME="prod_user"
export DB_PASSWORD="secure_password"
export JWT_SECRET="production_jwt_secret_key_at_least_256_bits_long"

mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 构建应用

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package
```

## 配置参数说明

### 核心配置参数

| 参数 | 描述 | 默认值 | 环境变量 |
|-----|------|-------|----------|
| `server.port` | 服务端口 | 8080 | `SERVER_PORT` |
| `spring.datasource.url` | 数据库连接URL | localhost:3306/blog_db | `DB_URL` |
| `spring.datasource.username` | 数据库用户名 | blog_user | `DB_USERNAME` |
| `spring.datasource.password` | 数据库密码 | your_password_here | `DB_PASSWORD` |
| `jwt.secret` | JWT密钥 | - | `JWT_SECRET` |
| `jwt.expiration` | JWT过期时间(秒) | 86400 | `JWT_EXPIRATION` |

### 功能配置参数

| 参数 | 描述 | 默认值 | 环境变量 |
|-----|------|-------|----------|
| `feature.article.max-title-length` | 文章标题最大长度 | 200 | `ARTICLE_MAX_TITLE_LENGTH` |
| `feature.article.max-content-length` | 文章内容最大长度 | 50000 | `ARTICLE_MAX_CONTENT_LENGTH` |
| `feature.article.auto-save-interval` | 自动保存间隔(秒) | 30 | `ARTICLE_AUTO_SAVE_INTERVAL` |

### 监控配置参数

| 参数 | 描述 | 默认值 | 环境变量 |
|-----|------|-------|----------|
| `management.endpoints.web.exposure.include` | 暴露的监控端点 | health,info,metrics | `ACTUATOR_ENDPOINTS` |
| `management.metrics.export.prometheus.enabled` | Prometheus指标导出 | false | `PROMETHEUS_ENABLED` |

## 安全配置

### 敏感信息处理

- **数据库密码**: 使用环境变量 `DB_PASSWORD`
- **JWT密钥**: 使用环境变量 `JWT_SECRET`
- **生产配置**: 所有敏感信息都通过环境变量外部化

### 配置文件安全

- 配置文件中不包含任何硬编码敏感信息
- 使用占位符 `${ENV_VAR:default}` 形式支持环境变量注入
- 生产环境配置强制要求外部提供敏感参数

## 日志配置

### 日志级别

- **开发环境**: DEBUG 级别，详细输出
- **测试环境**: INFO 级别，适度输出
- **生产环境**: WARN 级别，只输出警告和错误

### 日志文件

- 默认日志文件路径: `logs/blog-article-service.log`
- 支持环境变量 `LOG_FILE` 自定义路径
- 日志文件按日期滚动

## 监控和健康检查

### Actuator 端点

- 健康检查: `GET /actuator/health`
- 应用信息: `GET /actuator/info`
- 指标信息: `GET /actuator/metrics`
- Prometheus: `GET /actuator/prometheus` (生产环境可启用)

### 应用信息

访问 `/actuator/info` 可获取应用版本等信息:

```json
{
  "app": {
    "version": "0.1.0",
    "name": "博客系统核心文章管理模块",
    "description": "提供文章创建、编辑、发布等核心功能的微服务"
  }
}
```

## 开发指南

### 添加新配置

1. 在主配置文件中添加占位符配置
2. 在各环境配置文件中设置环境特定值
3. 更新本文档的配置参数说明

### 配置验证

```bash
# 验证配置文件语法
mvn spring-boot:run -Dspring.config.location=classpath:/application.yml

# 验证特定环境配置
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## 故障排除

### 常见问题

1. **YAML语法错误**: 检查缩进和重复键
2. **环境变量未设置**: 确认必要的环境变量已设置
3. **端口冲突**: 检查端口是否被占用
4. **数据库连接失败**: 验证数据库配置和网络连通性

### 调试技巧

```bash
# 查看生效的配置
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Ddebug

# 查看配置属性
curl http://localhost:8080/actuator/configprops
```

## 版本历史

- **v0.1.0** (2025-09-25): 初始版本，完成基础配置体系
