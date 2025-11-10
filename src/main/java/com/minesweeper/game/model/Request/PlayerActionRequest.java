package com.minesweeper.game.model.Request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 * 玩家操作请求
 */
public class PlayerActionRequest {
    
    @NotBlank(message = "游戏ID不能为空")
    private String gameId;

    @NotNull(message = "行坐标不能为空")
    @Min(value = 0, message = "行坐标不能为负数")
    private Integer row;

    @NotNull(message = "列坐标不能为空")
    @Min(value = 0, message = "列坐标不能为负数")
    private Integer col;

    @NotBlank(message = "操作类型不能为空")
    private String action; // CLICK, FLAG, UNFLAG

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    public PlayerActionRequest() {
    }

    public PlayerActionRequest(String gameId, Integer row, Integer col, String action, String userId) {
        this.gameId = gameId;
        this.row = row;
        this.col = col;
        this.action = action;
        this.userId = userId;
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
