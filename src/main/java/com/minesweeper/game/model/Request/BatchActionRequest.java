package com.minesweeper.game.model.Request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量操作请求（用于双击展开等高级操作）
 */
public class BatchActionRequest {
    
    @NotBlank(message = "游戏ID不能为空")
    private String gameId;

    @NotEmpty(message = "操作列表不能为空")
    @Valid
    private List<PlayerActionRequest> actions;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    public BatchActionRequest() {
    }

    public BatchActionRequest(String gameId, List<PlayerActionRequest> actions, String userId) {
        this.gameId = gameId;
        this.actions = actions;
        this.userId = userId;
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<PlayerActionRequest> getActions() {
        return actions;
    }

    public void setActions(List<PlayerActionRequest> actions) {
        this.actions = actions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
