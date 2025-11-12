package com.minesweeper.game.model.Entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类（JPA）
 */
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "total_games", nullable = false)
    private Integer totalGames = 0;

    @Column(name = "wins", nullable = false)
    private Integer wins = 0;

    @Column(name = "losses", nullable = false)
    private Integer losses = 0;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (totalGames == null) {
            totalGames = 0;
        }
        if (wins == null) {
            wins = 0;
        }
        if (losses == null) {
            losses = 0;
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

    /**
     * 转换为User对象（用于兼容现有代码）
     */
    public User toUser() {
        User user = new User();
        user.setUserId(this.userId);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setCreatedAt(this.createdAt);
        user.setTotalGames(this.totalGames);
        user.setWins(this.wins);
        user.setLosses(this.losses);
        return user;
    }

    /**
     * 从User对象创建UserEntity
     */
    public static UserEntity fromUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setUserId(user.getUserId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEmail(user.getEmail());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setTotalGames(user.getTotalGames());
        entity.setWins(user.getWins());
        entity.setLosses(user.getLosses());
        return entity;
    }
}

