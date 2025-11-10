package com.minesweeper.game.model.Request;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建房间请求
 */
public class CreateRoomRequest {
    
    @NotBlank(message = "房间名称不能为空")
    private String roomName;

    @NotNull(message = "最大玩家数不能为空")
    @Min(value = 2, message = "最大玩家数至少为2")
    @Max(value = 10, message = "最大玩家数最多为10")
    private Integer maxPlayers;

    @NotBlank(message = "房主ID不能为空")
    private String hostId;

    public CreateRoomRequest() {
    }

    public CreateRoomRequest(String roomName, Integer maxPlayers, String hostId) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.hostId = hostId;
    }

    // Getters and Setters
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
}
