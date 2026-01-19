package com.smartfridge.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果封装
 * 
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码：200=成功，其他=错误
     */
    private int code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    // ==================== 成功响应 ====================

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, System.currentTimeMillis());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis());
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data, System.currentTimeMillis());
    }

    // ==================== 错误响应 ====================

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null, System.currentTimeMillis());
    }

    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null, System.currentTimeMillis());
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null, System.currentTimeMillis());
    }

    // ==================== 快捷方法 ====================

    public static <T> Result<T> unauthorized() {
        return error(401, "未登录或登录已过期");
    }

    public static <T> Result<T> forbidden() {
        return error(403, "无权限访问");
    }

    public static <T> Result<T> notFound() {
        return error(404, "资源不存在");
    }

    public static <T> Result<T> badRequest(String msg) {
        return error(400, msg);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
