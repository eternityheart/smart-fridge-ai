package com.smartfridge.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ==================== 成功 ====================
    SUCCESS(200, "操作成功"),

    // ==================== 客户端错误 4xx ====================
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突"),
    VALIDATION_ERROR(422, "数据验证失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // ==================== 服务端错误 5xx ====================
    INTERNAL_ERROR(500, "系统繁忙，请稍后重试"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // ==================== 业务错误 1xxx ====================
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_EXISTS(1004, "用户名已存在"),

    // ==================== AI服务错误 2xxx ====================
    VISION_SERVICE_ERROR(2001, "视觉识别服务异常"),
    VISION_TIMEOUT(2002, "视觉识别超时"),
    LLM_SERVICE_ERROR(2003, "智能推荐服务异常"),
    LLM_TIMEOUT(2004, "智能推荐超时"),

    // ==================== 文件错误 3xxx ====================
    FILE_EMPTY(3001, "文件不能为空"),
    FILE_TOO_LARGE(3002, "文件大小超过限制"),
    FILE_TYPE_ERROR(3003, "文件类型不支持"),
    FILE_UPLOAD_ERROR(3004, "文件上传失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 提示信息
     */
    private final String msg;
}
