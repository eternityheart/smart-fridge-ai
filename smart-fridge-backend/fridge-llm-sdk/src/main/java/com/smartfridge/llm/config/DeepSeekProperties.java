package com.smartfridge.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek API 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepSeekProperties {

    /**
     * API Key
     */
    private String key;

    /**
     * 基础 URL
     */
    private String baseUrl = "https://api.deepseek.com/v1";

    /**
     * 请求超时时间 (毫秒)
     */
    private int timeout = 30000;

    /**
     * 最大 Token 数
     */
    private int maxTokens = 4096;

    /**
     * 模型名称
     */
    private String model = "deepseek-chat";

    /**
     * 温度参数 (0-2)
     */
    private double temperature = 0.7;
}
