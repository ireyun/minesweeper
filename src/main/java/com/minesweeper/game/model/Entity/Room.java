package com.minesweeper.game.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间实体类
 */
public class Room {
    private String roomId;
    private String roomName;
    private String hostId;
    private String hostUsername;
    private List<String> playerIds;
    private List<String> playerUsernames;
    private Integer maxPlayers;
    private String currentGameId; // 当前游戏ID，如果有
    private String status; // WAITING, PLAYING
    private Long createdAt; // 创建时间

    public Room() {
        this.playerIds = new ArrayList<>();
        this.playerUsernames = new ArrayList<>();
        this.status = "WAITING";
        this.createdAt = System.currentTimeMillis();
    }

    public Room(String roomId, String roomName, String hostId, String hostUsername, Integer maxPlayers) {
        this();
        this.roomId = roomId;
        this.roomName = roomName;
        this.hostId = hostId;
        this.hostUsername = hostUsername;
        this.maxPlayers = maxPlayers;
        this.playerIds.add(hostId);
        this.playerUsernames.add(hostUsername);
    }

    /**
     * 添加玩家
     */
    public boolean addPlayer(String playerId, String playerUsername) {
        if (playerIds.contains(playerId)) {
            return false; // 玩家已在房间中
        }
        if (playerIds.size() >= maxPlayers) {
            return false; // 房间已满
        }
        if (!status.equals("WAITING")) {
            return false; // 房间不在等待状态
        }
        playerIds.add(playerId);
        playerUsernames.add(playerUsername);
        return true;
    }

    /**
     * 移除玩家
     */
    public boolean removePlayer(String playerId) {
        int index = playerIds.indexOf(playerId);
        if (index == -1) {
            return false;
        }
        playerIds.remove(index);
        playerUsernames.remove(index);
        return true;
    }

    /**
     * 检查房间是否已满
     */
    public boolean isFull() {
        return playerIds.size() >= maxPlayers;
    }

    /**
     * 获取当前玩家数量
     */
    public Integer getCurrentPlayerCount() {
        return playerIds.size();
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public List<String> getPlayerUsernames() {
        return playerUsernames;
    }

    public void setPlayerUsernames(List<String> playerUsernames) {
        this.playerUsernames = playerUsernames;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}

