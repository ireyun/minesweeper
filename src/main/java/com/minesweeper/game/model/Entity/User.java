package com.minesweeper.game.model.Entity;

import java.util.Date;

/**
 * 用户实体类
 */
public class User {
    private String userId;
    private String username;
    private String password; // 实际应用中应该使用加密后的密码
    private String email;
    private Date createdAt; // 创建时间
    private Integer totalGames; // 总游戏数
    private Integer wins; // 胜利次数
    private Integer losses; // 失败次数

    public User() {
        this.totalGames = 0;
        this.wins = 0;
        this.losses = 0;
        this.createdAt = new Date();
    }

    public User(String userId, String username, String password, String email) {
        this();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * 计算胜率
     */
    public Double getWinRate() {
        if (totalGames == 0) {
            return 0.0;
        }
        return (double) wins / totalGames * 100;
    }

    /**
     * 增加游戏记录
     */
    public void addGameResult(boolean won) {
        totalGames++;
        if (won) {
            wins++;
        } else {
            losses++;
        }
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Integer totalGames) {
        this.totalGames = totalGames;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }
}

