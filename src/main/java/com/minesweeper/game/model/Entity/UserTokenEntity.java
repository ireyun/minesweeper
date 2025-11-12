package com.minesweeper.game.model.Entity;

import javax.persistence.*;

/**
 * 用户Token实体
 */
@Entity
@Table(name = "user_token")
public class UserTokenEntity {
    @Id
    @Column(name = "token", length = 255)
    private String token;

    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "expires_at", nullable = false)
    private Long expiresAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}

