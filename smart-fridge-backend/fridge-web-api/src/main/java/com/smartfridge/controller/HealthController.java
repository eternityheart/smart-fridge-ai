package com.smartfridge.controller;

import com.smartfridge.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供系统健康状态接口
 */
@Tag(name = "系统接口", description = "健康检查和系统信息")
@RestController
@RequestMapping("/api")
public class HealthController {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 健康检查接口
     * 前端和运维可用于检测服务是否正常
     */
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", appName);
        data.put("timestamp", System.currentTimeMillis());
        data.put("version", "1.0.0");
        return Result.success(data);
    }

    /**
     * 系统信息接口
     */
    @Operation(summary = "系统信息", description = "获取系统版本等信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "智能冰箱菜谱系统");
        data.put("version", "1.0.0");
        data.put("author", "SmartFridge Team");
        data.put("description", "基于AI的智能冰箱食材识别与菜谱推荐系统");
        return Result.success(data);
    }
}
