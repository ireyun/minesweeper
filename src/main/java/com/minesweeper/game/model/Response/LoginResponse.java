package com.minesweeper.game.model.Response;

/**
 * 登录响应
 */
public class LoginResponse {
    private String token; // JWT token
    private String userId;
    private String username;
    private Long expiresIn; // 过期时间（秒）
    private String tokenType; // 令牌类型，通常是 "Bearer"

    public LoginResponse() {
        this.tokenType = "Bearer";
    }

    public LoginResponse(String token, String userId, String username, Long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
