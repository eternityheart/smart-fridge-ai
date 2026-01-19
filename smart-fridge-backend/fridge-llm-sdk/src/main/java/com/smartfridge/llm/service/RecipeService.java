package com.smartfridge.llm.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.smartfridge.llm.client.DeepSeekClient;
import com.smartfridge.llm.template.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能菜谱服务
 * 基于 DeepSeek API 提供菜谱推荐功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final DeepSeekClient deepSeekClient;

    /**
     * 根据食材推荐菜谱
     *
     * @param ingredients  现有食材列表 (中文名)
     * @param preferences  用户偏好 (可选)
     * @return 推荐的菜谱 (JSON 格式)
     */
    public Map<String, Object> recommendRecipe(List<String> ingredients, String preferences) {
        log.info("开始推荐菜谱，食材数量: {}", ingredients.size());

        // 构建食材列表字符串
        String ingredientList = String.join("、", ingredients);
        
        // 构建用户请求
        String userMessage = PromptTemplates.buildRecipeRequest(ingredientList, preferences);

        // 调用 DeepSeek API
        String response = deepSeekClient.chat(PromptTemplates.RECIPE_SYSTEM, userMessage);

        log.debug("DeepSeek 原始响应: {}", response);

        // 解析 JSON 响应
        try {
            String jsonStr = extractJson(response);
            JSONObject jsonObj = JSONUtil.parseObj(jsonStr);
            
            // 转换为普通 Map
            Map<String, Object> result = new HashMap<>();
            for (String key : jsonObj.keySet()) {
                result.put(key, jsonObj.get(key));
            }
            return result;
        } catch (Exception e) {
            log.warn("解析菜谱响应失败，返回原始文本", e);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("recipeName", "AI 推荐");
            fallback.put("rawContent", response);
            return fallback;
        }
    }

    /**
     * 清洗食材识别结果
     * 将英文标签转换为标准化的中文食材名
     *
     * @param rawDetections 原始识别结果 (JSON 数组字符串)
     * @return 清洗后的食材列表
     */
    public List<Map<String, Object>> cleanIngredients(String rawDetections) {
        log.info("开始清洗食材识别结果");

        String userMessage = PromptTemplates.buildIngredientCleanRequest(rawDetections);
        String response = deepSeekClient.chat(PromptTemplates.INGREDIENT_CLEAN_SYSTEM, userMessage);

        try {
            String jsonStr = extractJson(response);
            JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
            
            // 手动转换为 List<Map>
            List<Map<String, Object>> result = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                for (String key : item.keySet()) {
                    map.put(key, item.get(key));
                }
                result.add(map);
            }
            return result;
        } catch (Exception e) {
            log.warn("解析食材清洗结果失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 从响应中提取 JSON 部分
     * 处理可能包含 markdown 代码块的情况
     */
    private String extractJson(String response) {
        String cleaned = response.trim();
        
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        return cleaned.trim();
    }
}
