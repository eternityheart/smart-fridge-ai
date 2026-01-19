package com.smartfridge.llm.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.smartfridge.common.exception.BusinessException;
import com.smartfridge.common.result.ResultCode;
import com.smartfridge.llm.config.DeepSeekProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API 客户端
 * 封装 HTTP 请求，提供智能对话能力
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {

    private final DeepSeekProperties properties;
    
    private static final String CHAT_ENDPOINT = "/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient httpClient;

    /**
     * 获取或创建 HTTP 客户端
     */
    private OkHttpClient getClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                    .build();
        }
        return httpClient;
    }

    /**
     * 发送聊天请求
     *
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @return AI 回复内容
     */
    public String chat(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return sendRequest(messages);
    }

    /**
     * 发送请求到 DeepSeek API
     */
    private String sendRequest(List<Map<String, String>> messages) {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", properties.getModel());
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", properties.getMaxTokens());
        requestBody.put("temperature", properties.getTemperature());

        String jsonBody = JSONUtil.toJsonStr(requestBody);
        
        Request request = new Request.Builder()
                .url(properties.getBaseUrl() + CHAT_ENDPOINT)
                .addHeader("Authorization", "Bearer " + properties.getKey())
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("DeepSeek API 调用失败: code={}, body={}", 
                        response.code(), response.body() != null ? response.body().string() : "null");
                throw new BusinessException(ResultCode.LLM_SERVICE_ERROR);
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            JSONObject json = JSONUtil.parseObj(responseBody);
            
            // 解析响应
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
                    
        } catch (IOException e) {
            log.error("DeepSeek API 网络异常", e);
            throw new BusinessException(ResultCode.LLM_TIMEOUT);
        }
    }
}
