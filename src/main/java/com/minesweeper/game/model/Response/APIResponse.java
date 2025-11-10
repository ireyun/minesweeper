package com.minesweeper.game.model.Response;

/**
 * 统一API响应包装类
 * @param <T> 响应数据类型
 */
public class APIResponse<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;

    public APIResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public APIResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(200, "成功", data);
    }

    /**
     * 成功响应（带自定义消息）
     */
    public static <T> APIResponse<T> success(String message, T data) {
        return new APIResponse<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> APIResponse<T> error(int code, String message) {
        return new APIResponse<>(code, message, null);
    }

    /**
     * 失败响应（默认500错误）
     */
    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>(500, message, null);
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
