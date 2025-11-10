package com.minesweeper.game.model.Request;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * 创建游戏请求
 */
public class CreateGameRequest {
    
    @NotNull(message = "游戏宽度不能为空")
    @Min(value = 5, message = "游戏宽度最小为5")
    @Max(value = 50, message = "游戏宽度最大为50")
    private Integer width;

    @NotNull(message = "游戏高度不能为空")
    @Min(value = 5, message = "游戏高度最小为5")
    @Max(value = 50, message = "游戏高度最大为50")
    private Integer height;

    @NotNull(message = "地雷数量不能为空")
    @Min(value = 1, message = "地雷数量至少为1")
    private Integer mineCount;

    private String difficulty; // EASY, MEDIUM, HARD, CUSTOM

    private String roomId; // 可选，如果是在房间中创建游戏

    private String userId; // 玩家ID

    public CreateGameRequest() {
    }

    public CreateGameRequest(Integer width, Integer height, Integer mineCount, String difficulty) {
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getMineCount() {
        return mineCount;
    }

    public void setMineCount(Integer mineCount) {
        this.mineCount = mineCount;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
