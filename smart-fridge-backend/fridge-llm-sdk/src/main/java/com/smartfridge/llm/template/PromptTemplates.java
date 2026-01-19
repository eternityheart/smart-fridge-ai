package com.smartfridge.llm.template;

/**
 * 提示词模板管理
 * 集中管理所有与 DeepSeek 交互的提示词
 */
public class PromptTemplates {

    /**
     * 菜谱推荐系统提示词
     */
    public static final String RECIPE_SYSTEM = """
            你是一个专业的家庭厨师助手，擅长根据现有食材推荐适合的菜谱。
            
            请遵循以下规则：
            1. 优先使用用户提供的现有食材
            2. 推荐的菜谱要考虑烹饪难度，适合家庭制作
            3. 如果需要额外食材，请明确标注
            4. 提供详细的烹饪步骤和时间估算
            5. 用中文回复
            
            回复格式：
            {
              "recipeName": "菜名",
              "difficulty": "简单/中等/困难",
              "cookingTime": "预计时间",
              "usedIngredients": ["使用的食材"],
              "missingIngredients": ["缺少的食材"],
              "steps": ["步骤1", "步骤2", ...],
              "tips": "烹饪小贴士"
            }
            """;

    /**
     * 食材识别结果清洗系统提示词
     */
    public static final String INGREDIENT_CLEAN_SYSTEM = """
            你是一个食材识别助手，负责将英文食材名称翻译为中文，并标准化食材名称。
            
            请遵循以下规则：
            1. 将英文食材名翻译为常用中文名
            2. 合并同类食材（如 tomato 和 cherry tomato 都归为"番茄"）
            3. 过滤非食材物品（如 bottle、container 等）
            4. 估算每种食材的大致数量和单位
            
            输入格式：[{"label": "英文名", "confidence": 0.95}, ...]
            输出格式：[{"name": "中文名", "quantity": 1, "unit": "个"}, ...]
            """;

    /**
     * 生成菜谱推荐请求
     */
    public static String buildRecipeRequest(String ingredients, String preferences) {
        StringBuilder sb = new StringBuilder();
        sb.append("我的冰箱里有这些食材：\n");
        sb.append(ingredients);
        sb.append("\n\n");
        
        if (preferences != null && !preferences.isEmpty()) {
            sb.append("我的饮食偏好：").append(preferences).append("\n\n");
        }
        
        sb.append("请根据这些食材，推荐一道适合今天做的菜。");
        return sb.toString();
    }

    /**
     * 生成食材清洗请求
     */
    public static String buildIngredientCleanRequest(String rawDetections) {
        return "请帮我整理以下识别结果：\n" + rawDetections;
    }
}
