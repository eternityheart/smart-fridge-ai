---
description: 企业级 Spring Boot 项目开发最佳实践与防护策略
---

# Enterprise Spring Boot Development Skill

本 Skill 总结了企业级 Spring Boot 项目开发的最佳实践，可用于任何后端项目。

---

## 项目结构规范

### Maven 多模块分层

```
project-name/
├── xxx-common/          # 公共模块：Result, Exception, Utils
├── xxx-domain/          # 领域层：Entity, Mapper, Repository
├── xxx-security/        # 安全模块：JWT, Spring Security
├── xxx-cache/           # 缓存模块：Redis, Caffeine
├── xxx-integration/     # 外部集成：第三方 API 封装
└── xxx-web-api/         # API 层：Controller, Service
```

---

## 统一响应格式

```java
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
```

---

## 全局异常处理

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统繁忙");
    }
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}
```

---

## CORS 配置

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .maxAge(3600);
    }
}
```

---

## 健康检查端点

```java
@RestController
public class HealthController {
    @GetMapping("/api/health")
    public Result<Map<String, Object>> health() {
        return Result.success(Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
```

---

## 抽屉式环境配置

### application.yml (主配置)
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

### application-dev.yml (开发环境)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xxx
    username: root
    password: ${DB_PASSWORD:password}
```

### application-prod.yml (生产环境)
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/xxx
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

---

## JWT 认证模板

```java
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private long expiration;
    
    public String generateToken(String userId) {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }
    
    public String getUserId(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

---

## 熔断降级 (Resilience4j)

```java
@Service
public class ExternalService {
    
    @CircuitBreaker(name = "external", fallbackMethod = "fallback")
    @TimeLimiter(name = "external")
    public CompletableFuture<String> callExternal() {
        // 调用外部 API
    }
    
    public CompletableFuture<String> fallback(Throwable t) {
        log.warn("降级处理: {}", t.getMessage());
        return CompletableFuture.completedFuture("默认值");
    }
}
```

---

## 多级缓存

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // L1: 本地缓存
        CaffeineCacheManager l1 = new CaffeineCacheManager();
        l1.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100));
        
        // L2: Redis
        RedisCacheManager l2 = RedisCacheManager.builder(factory).build();
        
        return new CompositeCacheManager(l1, l2);
    }
}
```

---

## 启动前检查清单

- [ ] MySQL 已启动
- [ ] Redis 已启动 (如需要)
- [ ] application.yml 配置正确
- [ ] 环境变量已设置
- [ ] 数据库表已创建
- [ ] /api/health 返回 200
