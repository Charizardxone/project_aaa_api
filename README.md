# Blog Article Service API

## Overview
Blog Article Service is a Spring Boot-based backend service for managing blog articles. It provides a robust configuration management system with multi-environment support.

## Configuration System

### Environment Profiles
The application supports three main environment profiles:

- **dev** - Development environment (default)
- **test** - Testing environment
- **prod** - Production environment

### Configuration Files

#### 1. application.yml (Main Configuration)
This is the base configuration file that contains:
- Application metadata
- Database connection placeholders
- Logging configuration
- CORS settings
- JWT configuration placeholders
- MyBatis-Plus configuration
- File upload settings
- Redis configuration (optional)
- Management endpoints configuration

#### 2. Environment-Specific Configurations

**application-dev.yml** (Development)
- Port: 8080
- Database: MySQL localhost:3306/blog_dev
- Debug mode: Enabled
- Swagger: Enabled
- Hikari pool: 10 max, 5 min
- Logging: DEBUG level for application code

**application-test.yml** (Testing)
- Port: 8081
- Database: MySQL test-db-host:3306/blog_test
- Debug mode: Disabled
- Swagger: Enabled
- Hikari pool: 20 max, 5 min
- Logging: INFO level for application code

**application-prod.yml** (Production)
- Port: 8080 (configurable via SERVER_PORT)
- Database: MySQL prod-db-host:3306/blog_prod
- Debug mode: Disabled
- Swagger: Disabled
- Hikari pool: 50 max, 10 min
- Enhanced logging with file rotation
- Production security settings

### Running the Application

#### Development Profile (Default)
```bash
mvn spring-boot:run
```

#### Test Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

#### Production Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Using Environment Variables
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://your-db-host:3306/blog_prod
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
mvn spring-boot:run
```

### Environment Variables

All configuration values can be overridden using environment variables:

#### Database Configuration
- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `DB_POOL_MAX` - Maximum pool size
- `DB_POOL_MIN` - Minimum pool size

#### Application Configuration
- `SERVER_PORT` - Server port (default: 8080)
- `CONTEXT_PATH` - Application context path
- `APP_DEBUG` - Debug mode (true/false)
- `LOG_LEVEL_ROOT` - Root logging level

#### Security Configuration
- `JWT_SECRET` - JWT secret key (required for production)
- `JWT_EXPIRATION` - JWT token expiration time
- `CORS_ALLOWED_ORIGINS` - CORS allowed origins

#### Redis Configuration (Optional)
- `REDIS_HOST` - Redis server host
- `REDIS_PORT` - Redis server port
- `REDIS_PASSWORD` - Redis password

### Security Notes

**⚠️ Important Security Practices:**

1. **Never commit sensitive credentials** to the repository
2. **Use environment variables** for production secrets
3. **Change default JWT secret** in production
4. **Configure proper CORS origins** for your frontend application
5. **Use SSL/TLS** in production environments

### Configuration Best Practices

1. **Development**: Use the dev profile with local database
2. **Testing**: Use the test profile with test database
3. **Production**: Use the prod profile with external secrets management
4. **Containerization**: Use environment variables for Docker/Kubernetes deployment
5. **CI/CD**: Inject secrets via environment variables or secret management systems

### Configuration Structure

The configuration follows a hierarchical structure:
1. `application.yml` - Base configuration with placeholders
2. `application-{profile}.yml` - Profile-specific overrides
3. Environment variables - Final overrides

This allows for flexible configuration management across different environments.

## Testing Configuration

Verify your configuration is working correctly:

1. **Profile Activation**: Check startup logs for active profile
2. **Port Configuration**: Verify correct port is used (dev: 8080, test: 8081)
3. **Database Connection**: Ensure database URLs match environment
4. **Logging Level**: Verify appropriate log levels are set
5. **Swagger Access**: Test Swagger UI at `/swagger-ui.html` (dev/test)

## Troubleshooting

### Common Issues

1. **Duplicate Key Error**: YAML syntax error with duplicate keys
2. **Port Already in Use**: Change port configuration or stop conflicting service
3. **Database Connection Failed**: Verify database credentials and URL
4. **Missing Environment Variables**: Set required environment variables for production

### Debug Mode

Enable debug mode in development:
```yaml
app:
  debug: true
```

Or via environment variable:
```bash
export APP_DEBUG=true
```
