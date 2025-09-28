# Blog Article Service API

## 项目简介

博客系统核心文章管理模块的后端API服务，基于Spring Boot 3.x构建，支持多环境配置管理。

## 技术栈

- **框架**: Spring Boot 3.5.5
- **数据库**: MySQL 8.0+ (支持MyBatis-Plus)
- **构建工具**: Maven
- **Java版本**: JDK 17

## 环境配置

### 配置文件结构

项目使用Spring Boot的多环境配置机制，包含以下配置文件：

1. **application.yml** - 主配置文件，包含通用配置和环境激活设置
2. **application-dev.yml** - 开发环境配置
3. **application-test.yml** - 测试环境配置
4. **application-prod.yml** - 生产环境配置

### 环境切换

#### 开发环境
```bash
mvn spring-boot:run
# 或者
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```
- **端口**: 8081
- **数据库**: blog_dev
- **日志级别**: DEBUG
- **特性**: 开启热重载、Swagger UI、详细日志

#### 测试环境
```bash
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```
- **端口**: 8082
- **数据库**: blog_test
- **日志级别**: INFO
- **特性**: 禁用Swagger、限制CORS、生产级配置

#### 生产环境
```bash
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
- **端口**: 8080
- **数据库**: 通过环境变量配置
- **日志级别**: WARN
- **特性**: 安全配置、性能优化、监控指标

### 环境变量配置

所有敏感信息通过环境变量注入：

| 环境变量 | 默认值 | 描述 |
|---------|--------|------|
| `DB_URL` | jdbc:mysql://localhost:3306/blog_db | 数据库连接URL |
| `DB_USERNAME` | root | 数据库用户名 |
| `DB_PASSWORD` | password | 数据库密码 |
| `JWT_SECRET` | your-secret-key-here | JWT密钥 |
| `JWT_EXPIRATION` | 86400 | JWT过期时间(秒) |
| `SERVER_PORT` | 8080 | 服务端口 |
| `LOG_LEVEL_ROOT` | INFO | 根日志级别 |

### 配置规范

#### 命名空间约定
- `app.*` - 应用级配置
- `spring.*` - Spring框架配置
- `server.*` - 服务器配置
- `logging.*` - 日志配置
- `management.*` - 监控管理配置

#### 安全原则
- 所有敏感信息使用环境变量或占位符
- 生产环境禁止硬编码密码
- 配置文件使用YAML格式，支持层次结构

## 启动方式

### 开发模式
```bash
mvn spring-boot:run
```

### 生产模式
```bash
mvn clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker运行
```bash
docker build -t blog-api .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://db:3306/blog_prod \
  -e DB_USERNAME=prod_user \
  -e DB_PASSWORD=secure_password \
  blog-api
```

## 监控与维护

### 健康检查
```bash
curl http://localhost:8080/actuator/health
```

### 应用信息
```bash
curl http://localhost:8080/actuator/info
```

### 监控指标
```bash
curl http://localhost:8080/actuator/metrics
```

## 开发指南

### 依赖管理
项目使用Maven管理依赖，主要依赖包括：
- Spring Boot Web Starter
- Spring Boot Data JPA
- MyBatis-Plus
- Lombok
- Spring Boot Test

### 测试
```bash
mvn test
```

### 代码规范
- 遵循Spring Boot最佳实践
- 使用Lombok减少样板代码
- 配置文件使用统一的注释格式
- 环境相关配置必须通过环境变量注入

## 问题排查

### 常见问题
1. **端口冲突**: 检查`SERVER_PORT`环境变量
2. **数据库连接失败**: 检查数据库连接信息和权限
3. **配置文件解析错误**: 检查YAML语法和缩进
4. **日志目录不存在**: 生产环境需要创建`/var/log/blog`目录

### 调试模式
开发环境启用调试模式：
```bash
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --debug
```

## 版本历史

- **v0.1.0** - 初始配置架构，支持多环境部署

## 许可证

本项目采用MIT许可证。
