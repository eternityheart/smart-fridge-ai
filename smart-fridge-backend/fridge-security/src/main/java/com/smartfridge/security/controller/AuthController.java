package com.smartfridge.security.controller;

import com.smartfridge.common.result.Result;
import com.smartfridge.security.dto.LoginRequest;
import com.smartfridge.security.dto.RegisterRequest;
import com.smartfridge.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 登录、注册接口
 */
@Tag(name = "认证接口", description = "用户登录、注册")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "登录", description = "用户名密码登录，返回 JWT Token")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(request);
        return Result.success("登录成功", result);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "注册", description = "用户注册，注册成功自动登录返回 Token")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> result = authService.register(request);
        return Result.success("注册成功", result);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "当前用户", description = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser(@RequestAttribute("userId") Long userId,
                                                       @RequestAttribute("username") String username) {
        Map<String, Object> user = Map.of(
                "userId", userId,
                "username", username
        );
        return Result.success(user);
    }
}
