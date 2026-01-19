package com.smartfridge.controller;

import com.smartfridge.common.result.Result;
import com.smartfridge.llm.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能菜谱控制器
 */
@Tag(name = "智能菜谱", description = "基于 AI 的菜谱推荐")
@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * 根据食材推荐菜谱
     */
    @Operation(summary = "推荐菜谱", description = "根据现有食材智能推荐菜谱")
    @PostMapping("/recommend")
    public Result<Map<String, Object>> recommendRecipe(
            @Parameter(description = "食材列表") @RequestBody List<String> ingredients,
            @Parameter(description = "用户偏好") @RequestParam(required = false) String preferences) {
        
        Map<String, Object> recipe = recipeService.recommendRecipe(ingredients, preferences);
        return Result.success("推荐成功", recipe);
    }

    /**
     * 快速推荐 - 使用用户库存中的食材
     */
    @Operation(summary = "快速推荐", description = "使用用户冰箱中的食材自动推荐菜谱")
    @GetMapping("/quick")
    public Result<Map<String, Object>> quickRecommend(
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        // TODO: 从库存中获取食材列表
        List<String> ingredients = List.of("鸡蛋", "番茄", "葱", "盐");
        
        Map<String, Object> recipe = recipeService.recommendRecipe(ingredients, null);
        return Result.success("推荐成功", recipe);
    }
}
