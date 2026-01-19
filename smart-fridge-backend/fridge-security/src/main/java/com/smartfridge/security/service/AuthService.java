package com.smartfridge.security.service;

import com.smartfridge.common.exception.BusinessException;
import com.smartfridge.common.result.ResultCode;
import com.smartfridge.common.utils.JwtUtils;
import com.smartfridge.domain.entity.User;
import com.smartfridge.domain.mapper.UserMapper;
import com.smartfridge.security.dto.LoginRequest;
import com.smartfridge.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 * 处理登录、注册逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        log.info("用户登录成功: username={}", user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }

    /**
     * 用户注册
     */
    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(request.getUsername());
        if (existing != null) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());

        userMapper.insert(user);

        log.info("用户注册成功: username={}", user.getUsername());

        // 自动登录，返回 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }
}
