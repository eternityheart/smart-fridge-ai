---
description: DeepSeek LLM API 集成最佳实践 - 提示词工程与熔断降级
---

# DeepSeek LLM Integration Skill

本 Skill 指导如何在 Spring Boot 中集成 DeepSeek API。

---

## API 配置

```yaml
# application.yml
deepseek:
  api:
    key: ${DEEPSEEK_API_KEY}
    base-url: https://api.deepseek.com/v1
    timeout: 30000
    max-tokens: 4096
```

---

## Feign Client 定义

```java
@FeignClient(
    name = "deepseek",
    url = "${deepseek.api.base-url}",
    configuration = DeepSeekConfig.class
)
public interface DeepSeekClient {
    
    @PostMapping("/chat/completions")
    ChatCompletionResponse chat(@RequestBody ChatCompletionRequest request);
}

@Configuration
public class DeepSeekConfig {
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> template.header("Authorization", "Bearer " + apiKey);
    }
}
```

---

## 请求/响应对象

```java
@Data
public class ChatCompletionRequest {
    private String model = "deepseek-chat";
    private List<Message> messages;
    private double temperature = 0.7;
    private int max_tokens = 4096;
    
    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;  // system, user, assistant
        private String content;
    }
}

@Data
public class ChatCompletionResponse {
    private List<Choice> choices;
    
    @Data
    public static class Choice {
        private Message message;
    }
}
```

---

## 提示词模板管理

```java
@Component
public class PromptManager {
    
    private static final String RECIPE_SYSTEM_PROMPT = """
        你是一个五星级大厨和营养师。
        请严格按照 JSON 格式返回数据，格式如下：
        {
          "recipes": [
            {
              "name": "菜名",
              "difficulty": "简单|中等|复杂",
              "cookTime": "烹饪时间(分钟)",
              "steps": ["步骤1", "步骤2"],
              "missingIngredients": ["缺少的配料"]
            }
          ]
        }
        """;
    
    public ChatCompletionRequest buildRecipeRequest(
            List<String> ingredients, 
            String preferences) {
        
        String userPrompt = String.format(
            "我的冰箱里有：%s\n我的需求是：%s\n请推荐3个菜谱。",
            String.join(", ", ingredients),
            preferences
        );
        
        return ChatCompletionRequest.builder()
            .messages(List.of(
                new Message("system", RECIPE_SYSTEM_PROMPT),
                new Message("user", userPrompt)
            ))
            .build();
    }
}
```

---

## 服务层实现 (带熔断)

```java
@Service
@Slf4j
public class CookingBrainService {
    
    @Autowired
    private DeepSeekClient client;
    
    @Autowired
    private PromptManager promptManager;
    
    @Autowired
    private RecipeCacheService cacheService;
    
    @CircuitBreaker(name = "deepseek", fallbackMethod = "fallbackRecipes")
    @TimeLimiter(name = "deepseek")
    public CompletableFuture<List<Recipe>> generateRecipes(
            List<String> ingredients, 
            String preferences) {
        
        return CompletableFuture.supplyAsync(() -> {
            ChatCompletionRequest request = 
                promptManager.buildRecipeRequest(ingredients, preferences);
            
            ChatCompletionResponse response = client.chat(request);
            String content = response.getChoices().get(0).getMessage().getContent();
            
            return parseRecipes(content);
        });
    }
    
    // 降级方法：返回缓存的热门菜谱
    public CompletableFuture<List<Recipe>> fallbackRecipes(
            List<String> ingredients, 
            String preferences,
            Throwable t) {
        
        log.warn("DeepSeek API 熔断，返回缓存菜谱: {}", t.getMessage());
        return CompletableFuture.completedFuture(
            cacheService.getPopularRecipes(ingredients)
        );
    }
}
```

---

## JSON 解析

```java
private List<Recipe> parseRecipes(String jsonContent) {
    try {
        // 清理 Markdown 代码块
        String cleaned = jsonContent
            .replaceAll("```json\\n?", "")
            .replaceAll("```\\n?", "")
            .trim();
        
        JsonNode root = objectMapper.readTree(cleaned);
        JsonNode recipes = root.get("recipes");
        
        return objectMapper.convertValue(
            recipes, 
            new TypeReference<List<Recipe>>() {}
        );
    } catch (Exception e) {
        log.error("解析菜谱失败: {}", jsonContent, e);
        return Collections.emptyList();
    }
}
```

---

## Resilience4j 配置

```yaml
resilience4j:
  circuitbreaker:
    instances:
      deepseek:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
  
  timelimiter:
    instances:
      deepseek:
        timeout-duration: 30s
```

---

## 使用示例

```java
@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    
    @Autowired
    private CookingBrainService brainService;
    
    @PostMapping("/recommend")
    public Result<List<Recipe>> recommend(@RequestBody RecipeRequest request) {
        List<Recipe> recipes = brainService
            .generateRecipes(request.getIngredients(), request.getPreferences())
            .get();
        
        return Result.success(recipes);
    }
}
```

---

## 常见问题

| 问题 | 解决方案 |
|-----|---------|
| API Key 无效 | 检查环境变量 |
| 超时 | 增加 timeout 配置 |
| JSON 解析失败 | 检查 Prompt 是否要求 JSON 格式 |
| 熔断打开 | 检查 API 余额 / 网络 |
